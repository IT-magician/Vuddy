package com.b305.buddy

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.b305.buddy.databinding.ActivitySignupBinding
import com.b305.buddy.model.AuthRequest
import com.b305.buddy.model.AuthResponse
import com.b305.buddy.model.Token
import com.b305.buddy.util.RetrofitAPI
import com.b305.buddy.util.SharedManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    
    lateinit var binding: ActivitySignupBinding
    
    private val sharedManager: SharedManager by lazy { SharedManager(this) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // true : 회원가입, false : 로그인
        var isSignup = true
        
        binding.btnSignup.setOnClickListener {
            isSignup = true
            binding.btnSignup.setBackgroundResource(R.color.selected)
            binding.btnLogin.setBackgroundResource(R.color.unselected)
        }
        
        binding.btnLogin.setOnClickListener {
            isSignup = false
            binding.btnSignup.setBackgroundResource(R.color.unselected)
            binding.btnLogin.setBackgroundResource(R.color.selected)
        }
        
        binding.btnSignupOk.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            val password = binding.etPassword.text.toString()
            val userData = AuthRequest(nickname, password)
            
            if (isSignup) {
                signup(userData)
            } else {
                login(userData)
            }
        }
        
        binding.btnSignupCancel.setOnClickListener {
            finish()
        }
        
        binding.btnCheck.setOnClickListener {
            val accessToken: String = sharedManager.getCurrentToken().accessToken.toString()
            val refreshToken: String = sharedManager.getCurrentToken().refreshToken.toString()
            Toast.makeText(this, accessToken + refreshToken, Toast.LENGTH_SHORT).show()
        }
        
        binding.btnDelete.setOnClickListener {
            sharedManager.removeCurrentToken()
        }
    }
    
    private fun login(authRequest: AuthRequest) {
        val service = RetrofitAPI.authService
        
        service.login(authRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    
                    val accessToken: String = result?.accessToken.toString()
                    val refreshToken: String = result?.refreshToken.toString()
                    val token: Token = Token(accessToken, refreshToken)
                    
                    sharedManager.saveCurrentToken(token)
                    Toast.makeText(this@SignupActivity, accessToken + refreshToken, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        })
        
    }
    
    private fun signup(authRequest: AuthRequest) {
        val service = RetrofitAPI.authService
        
        service.signup(authRequest).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val result = response.body()
                    
                    val accessToken: String = result?.accessToken.toString()
                    val refreshToken: String = result?.refreshToken.toString()
                    val token: Token = Token(accessToken, refreshToken)
                    
                    sharedManager.saveCurrentToken(token)
                    Toast.makeText(this@SignupActivity, accessToken + refreshToken, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@SignupActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }
}