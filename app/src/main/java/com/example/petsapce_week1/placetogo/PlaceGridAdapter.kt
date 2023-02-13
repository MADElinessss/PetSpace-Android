package com.example.petsapce_week1.placetogo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import com.example.petsapce_week1.R
import com.example.petsapce_week1.network.AccomoService
import com.example.petsapce_week1.network.RetrofitHelper
import com.example.petsapce_week1.vo.FavoriteBackendResponse
import kotlinx.android.synthetic.main.placetogo_grid_itemlist.view.*
import retrofit2.Retrofit
import java.io.Serializable


class PlaceGridAdapter(val context: Context, var img_list:  Array<Int>, val accessToken: String) : BaseAdapter() {

    override fun getCount(): Int {
        return img_list.size
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    // ========== 백엔드 연동 부분 ===========
    private var retrofit: Retrofit = RetrofitHelper.getRetrofitInstance()
    // 기본 숙소 정보 불러올때 호출
    var api : AccomoService = retrofit.create(AccomoService::class.java)

    lateinit var postaccessToken : String

    var accommoList = mutableListOf<FavoriteBackendResponse.Favorite>()

    @SuppressLint("ViewHolder", "InflateParams", "CutPasteId")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {

        Log.d("함께 갈 곳", "111")

        val view : View = convertView ?: LayoutInflater.from(context).inflate(R.layout.placetogo_grid_itemlist, parent, false)
        val imageView = view.findViewById<ImageButton>(R.id.place_menu)
        val image = img_list[position]
        imageView.setImageResource(image)

        var region : String ?= null

        when (position) {
            0 -> {
                region = "SEOUL"
            }
            1 -> {
                region = "BUSAN"
            }
            2 -> {
                region = "CHUNGCHENOGDO"
            }
            3 -> {
                region = "JEJUDO"
            }
            4 -> {
                region = "DAEGU"
            }
            5 -> {
                region = "GYEONGGIDO"
            }
            6 -> {
                region = "GWANGJU"
            }
            7 -> {
                region = "JEOLLADO"
            }
        }

        Log.d("함께 갈 곳", accessToken)
        view?.place_menu?.setImageResource(img_list[position])
        Log.d("함께 갈 곳 어답터", img_list.toString())

        val button = view?.findViewById<ImageButton>(R.id.place_menu)
        var isLast : Boolean = false


        button?.setOnClickListener {
            postaccessToken = "Bearer $accessToken"
            if (region != null) {
                api.getFavorites(postaccessToken, region, 0, 4)
                    .enqueue(object : retrofit2.Callback<FavoriteBackendResponse> {
                        override fun onResponse(
                            call: retrofit2.Call<FavoriteBackendResponse>,
                            response: retrofit2.Response<FavoriteBackendResponse>
                        ) {
                            Log.d("함께 갈 곳 성공", response.toString())
                            Log.d("함께 갈 곳", response.body().toString())
                            if (response.body()?.result != null) {
                                accommoList = response.body()?.result?.favorites?.toMutableList()!!
                                Log.d("함께 갈 곳 데이터2", "$accommoList")
                                Log.d("함께 갈 곳 region", "$region")
                                if (response.body()!!.result.isLast) {
                                    isLast = true
                                }
                                val nextScreenIntent =
                                    Intent(context, PlaceToGoRegionActivity::class.java).apply {
                                        putExtra("accommoList", accommoList as Serializable)
                                        putExtra("isLast", isLast)
                                        putExtra("accessToken", postaccessToken)
                                        putExtra("region", region)
                                    }
                                context.startActivity(nextScreenIntent)
                                Log.d("함께 ㅇㅇ","ㅇㅇ")
                            } else {
                                // ** no place to go fragment 로 이동하면 되는 코드....
        //                            val fragmentTransaction = fragmentManager.beginTransaction()
        //                            val newFragment = NoPlaceToGoFragment()
        //                            fragmentTransaction.add(R.id.placetogoLayout, newFragment)
        //                            fragmentTransaction.addToBackStack(null)
        //                            fragmentTransaction.commit()
        //                            Log.d("함께 갈 곳 no", "noplacetogo")

        //                        val intent = Intent(context, NoPlaceToGoFragment::class.java)
        //                        Log.d("함께 갈 곳 인텐t", "ㅇㅇ")
        //                        context.startActivity(intent)
        //                        Log.d("함께 갈 곳 start", "ㅇㅇ")

        //                        val newFragment = NoPlaceToGoFragment.newInstance(getItem(position) as String)
        //                        fragment.parentFragmentManager
        //                            .beginTransaction()
        //                            .replace(R.id.placetogoLayout, newFragment)
        //                            .addToBackStack(null)
        //                            .commit()
                            }

                        }

                        override fun onFailure(
                            call: retrofit2.Call<FavoriteBackendResponse>,
                            t: Throwable
                        ) {
                            Log.d(" 함께 갈 곳 호출 실패", "ㅠㅠ")
                            Log.d("함께 에러 메시지", t.toString())
        //                    val intent = Intent(context, NoPlaceToGoFragment::class.java)
        //                    context.startActivity(intent)
                            // ** no place to go fragment 로 이동하면 되는 코드....
        //                        val fragmentTransaction = fragmentManager.beginTransaction()
        //                        val newFragment = NoPlaceToGoFragment()
        //                        fragmentTransaction.add(R.id.placetogoLayout, newFragment)
        //                        fragmentTransaction.addToBackStack(null)
        //                        fragmentTransaction.commit()
        //                        Log.d("함께 갈 곳 no", "noplacetogo")
                            val noplacetogofragment = NoPlaceToGoFragment()
                            val placetogofragment = PlaceToGoFragment()
                            placetogofragment.parentFragmentManager
                                .beginTransaction()
                                .addToBackStack(null)
                                .replace(R.id.fragmentContainerView, noplacetogofragment)
                                .commit()
                        }
                    })
            }
        }
        return view
    }
}