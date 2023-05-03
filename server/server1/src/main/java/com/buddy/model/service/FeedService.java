package com.buddy.model.service;

import com.buddy.model.dto.FeedWithTagsDto;
import com.buddy.model.dto.FeedWithTagsListDto;
import com.buddy.model.dto.request.FeedEditReq;
import com.buddy.model.dto.response.SingleFeedRes;
import com.buddy.model.dto.response.UserFeedsRes;
import com.buddy.model.entity.*;
import com.buddy.model.repository.FeedLikesRepository;
import com.buddy.model.repository.FeedRepository;
import com.buddy.model.repository.TaggedFriendsRepository;
import com.buddy.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

    private final FeedRepository feedRepository;
    private final TaggedFriendsRepository taggedFriendsRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final FeedLikesRepository feedLikesRepository;

    @Transactional
    public void saveFeed(Feed feed) {
        feedRepository.save(feed);
    }

    public List<Feed> findAllByUserId(Long userId) {
        return feedRepository.findAllByUserId(userId);
    }

    public List<UserFeedsRes> changeAllFeedsToDto(List<Feed> allFeeds) {

        List<UserFeedsRes> userFeedsResList = new ArrayList<>();

        for (Feed feed : allFeeds) {
            UserFeedsRes userFeedsRes = new UserFeedsRes(feed.getId(), feed.getContent(), feed.getMainImg());
            userFeedsResList.add(userFeedsRes);
        }

        return userFeedsResList;

    }

    public List<Feed> findAllByToken(String token) {
        Long userId = userService.findByToken(token).getId();
        return feedRepository.findAllByUserId(userId);
    }


    public SingleFeedRes findOneByFeedId(Long feedId, String nickname) {
        List<FeedWithTagsDto> result = feedRepository.findOneWithTags(feedId);
        if (result.isEmpty()) {
            return null;
        }

        Feed feed = result.get(0).getFeed();

        List<String> taggedFriendsList = result.stream()
                .map(FeedWithTagsDto::getTaggedFriend)
                .filter(Objects::nonNull)
                .map(TaggedFriends::getNickname)
                .distinct()
                .collect(Collectors.toList());

        boolean isLiked = result.stream()
                .map(FeedWithTagsDto::getFeedLikes)
                .filter(Objects::nonNull)
                .map(FeedLikes::getUser)
                .filter(Objects::nonNull)
                .anyMatch(user -> Objects.equals(user.getNickname(), nickname));

        Set<Long> likesCount = result.stream()
                .map(FeedWithTagsDto::getFeedLikes)
                .filter(Objects::nonNull)
                .map(FeedLikes::getId)
                .collect(Collectors.toSet());

        Set<Long> commentsCount = result.stream()
                .map(FeedWithTagsDto::getComments)
                .filter(Objects::nonNull)
                .map(Comments::getId)
                .collect(Collectors.toSet());

        return SingleFeedRes.builder()
                .feedId(feed.getId())
                .nickname(feed.getNickname())
                .content(feed.getContent())
                .location(feed.getLocation())
                .createdAt(feed.getCreatedAt().toString())
                .updatedAt(feed.getUpdatedAt().toString())
                .isLiked(isLiked)
                .taggedFriends(taggedFriendsList)
                .likesCount((long) likesCount.size())
                .commentsCount((long) commentsCount.size())
                .build();
    }

    @Transactional
    public void editFeed(Long feedId, FeedEditReq req) {
        Feed feed = feedRepository.findFeedWithTagsListById(feedId)
                .map(FeedWithTagsListDto::getFeed)
                .orElseThrow(() -> new IllegalArgumentException("해당 피드가 존재하지 않습니다."));

        feed.updateContentAndLocation(req.getContent(), req.getLocation());

        taggedFriendsRepository.deleteByFeedId(feedId);

        // 이미지는 구현 예정
        // imagesRepository.deleteByFeedId(feedId);

        List<String> userNicknames = req.getTags();

        Map<String, User> userMap = userRepository.findAllByNicknameIn(userNicknames).stream()
                .collect(Collectors.toMap(User::getNickname, user -> user));

        List<TaggedFriends> newTaggedFriends = userNicknames.stream()
                .map(tag -> {
//                    User taggedUser = userRepository.findByNickname(tag);
                    User taggedUser = userMap.get(tag);
                    return req.toTagEntity(feed, taggedUser);
                })
                .collect(Collectors.toList());
        taggedFriendsRepository.saveAll(newTaggedFriends);
    }

    public List<Feed> findAllByNickname(String nickname) {
        return feedRepository.findAllByNickname(nickname);
    }

    @Transactional
    public String likeFeed(Long feedId, String nickname) {

        Feed feed = feedRepository.findById(feedId)
                .orElseThrow(() -> new IllegalArgumentException("해당 피드가 존재하지 않습니다."));

        User user = userRepository.findByNickname(nickname);

        Optional<FeedLikes> findFeedLike = feedLikesRepository.findByFeedAndUser(feed, user);

        if (findFeedLike.isPresent()) {
            feedLikesRepository.delete(findFeedLike.get());
            return "좋아요 취소 성공";
        } else {
            FeedLikes feedLikes = FeedLikes.createLike(feed, user);
            feedLikesRepository.save(feedLikes);
            return "좋아요 성공";
        }
    }
}
