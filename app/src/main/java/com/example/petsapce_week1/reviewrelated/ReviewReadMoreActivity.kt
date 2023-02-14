package com.example.petsapce_week1.reviewrelated

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.petsapce_week1.databinding.ActivityReviewReadMoreBinding
import com.example.petsapce_week1.network.RetrofitHelper
import com.example.petsapce_week1.network.ReviewGETAPI
import com.example.petsapce_week1.vo.ReviewGetData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit


class ReviewReadMoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReviewReadMoreBinding
    private var retrofit: Retrofit = RetrofitHelper.getRetrofitInstance()
    var api: ReviewGETAPI = retrofit.create(ReviewGETAPI::class.java)

    private var is_last: Boolean = false
    private var page_num: Int = -1
    private var size: Int = 5

    private lateinit var adapter: ReviewAdapter

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewReadMoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ReviewAdapter()
        binding.rvReview.adapter = adapter

        loadData()
        initScrollListener()


        binding.btnReviewClose.setOnClickListener {
            finish()
        }
    }

    private fun loadData() {
        api.getReviews(roomId = 1, getPage(), size).enqueue(object : Callback<ReviewGetData> {
            override fun onResponse(call: Call<ReviewGetData>, response: Response<ReviewGetData>) {
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null && response.isSuccessful) {
                        is_last = body.result.isLast
                        page_num = body.result.page
                        adapter.setReviews(body.result.reviews)
                        Log.d("body1", body.result.toString())
                    }
                    } else {
                        Log.d("바디", response.body().toString())
                    Log.d("error", "err")
                    // 통신 에러
                    }
                }
            override fun onFailure(call: Call<ReviewGetData>, t: Throwable) {
                Log.d("this is error", t.toString())
            }
        })
    }

    // 리사이클러뷰에 더 보여줄 데이터를 로드하는 경우
    private fun loadMoreReviews() {
        adapter.setLoadingView(true)

        // 너무 빨리 데이터가 로드되면 스크롤 되는 Ui 를 확인하기 어려우므로,
        // Handler 를 사용하여 1초간 postDelayed 시켰다
        Handler(Looper.getMainLooper()).postDelayed({
            api.getReviews(roomId = 1, getPage(), size)
                .enqueue(object : Callback<ReviewGetData> {
                    override fun onResponse(
                        call: Call<ReviewGetData>,
                        response: Response<ReviewGetData>
                    ) {
                        val body = response.body()
                        if (body != null && response.isSuccessful) {
                            is_last = body.result.isLast
                            page_num = body.result.page
                            adapter.run {
                                setLoadingView(false)
                                addReviews(body.result.reviews)
                            }
                        } else {
                            // 통신 에러
                            if (body != null) {
                                Log.d("body2", body.result.reviews.toString())
                            }
                        }
                    }
                    override fun onFailure(call: Call<ReviewGetData>, t: Throwable) {
                        // 통신 에러
                        Log.d("body", t.toString())
                    }
                })
        }, 1000)
    }

    private fun initScrollListener() {
        binding.rvReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = binding.rvReview.layoutManager

                // hasNextPage() -> 다음 페이지가 있는 경우
                if (hasNextPage()) {
                    val lastVisibleItem = (layoutManager as LinearLayoutManager)
                        .findLastCompletelyVisibleItemPosition()

                    // 마지막으로 보여진 아이템 position 이
                    // 전체 아이템 개수보다 모자란 경우, 데이터를 loadMore 한다
                    if (layoutManager.itemCount <= lastVisibleItem) {
                        loadMoreReviews()
                        setHasNextPage(false)
                    }
                }
            }
        })
    }

    private fun getPage(): Int {
        page_num++
        return page_num
    }

    private fun hasNextPage(): Boolean {
        return is_last
    }

    private fun setHasNextPage(b: Boolean) {
        is_last = b
    }
}
