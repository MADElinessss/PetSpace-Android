package com.example.petsapce_week1.home

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.petsapce_week1.databinding.ActivityHomeResearchBinding
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_home_research.*

class HomeResearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeResearchBinding
    lateinit var viewModel: ResViewModel
    lateinit var city:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeResearchBinding.inflate(layoutInflater)

        setContentView(binding.root)


        //뷰페이져 부분 터치하면 바로 액티비티 텍스트에 반영 viewmodel $$ observer패턴
        viewModel = ViewModelProvider(this).get(ResViewModel::class.java)
        //화면에 바로 뷰바인딩
        val nameObserver = Observer<String> { it ->
            binding.textWhereShow.setText(it.toString())
            binding.textWhereShow.setSelection(binding.textWhereShow.length())
//            city = it.toString()
        }
        viewModel.currentValue.observe(this, nameObserver)

        /*    val nameObserver2 = Observer<String> { curText ->
                binding.textWhere.text = curText.toString()
            }*/
        //옵저버 이벤트가 생길때마다 알려줌
//        viewModel.currentValue.observe(this,nameObserver2)

        /* binding.DataBinding
         binding.textWhere = viewModel*/

        // viewpager && adapter
        val mainVPAdapter = HomeResVPAdapter(this)
        val viewPager = binding.viewpager
        val dotsIndicator = binding.dotsIndicator
        viewPager.adapter = mainVPAdapter
        dotsIndicator.attachTo(viewPager)


//        initViewPager()
        initPerson()
        initCalendar()
        initDataBinding()
        initReset()
//        showProfileDialog()

    }


    @SuppressLint("SetTextI18n")
    private fun initDataBinding() {
        //성인
        val nameObserver = Observer<Int> { curAdult ->
            binding.textPerson.text =
                "성인 ${curAdult}명, 아동 ${viewModel.curChild.value}명, 반려동물 ${viewModel.curAnimal.value}마리"
        }
        viewModel.curAdult.observe(this, nameObserver)

        //아동
        viewModel.curChild.observe(this, Observer { curChild ->
            binding.textPerson.text =
                "성인 ${viewModel.curAdult.value}명, 아동 ${curChild}명, 반려동물 ${viewModel.curAnimal.value}마리"
        })

        //동물
        viewModel.curAnimal.observe(this, Observer { curAnimal ->
            binding.textPerson.text =
                "성인 ${viewModel.curAdult.value}명, 아동 ${viewModel.curChild.value}명, 반려동물 ${curAnimal}마리"
        })

        //날짜
        val calStartObserver = Observer<String> { it ->
            binding.textCalendar.text = "$it ~ ${viewModel.curEndDate.value}"
        }
        viewModel.curStartDate.observe(this, calStartObserver)


        val calEndObserver = Observer<String> { curEndDate ->
            binding.textCalendar.text = "${viewModel.curStartDate.value} ~ $curEndDate"
        }
        viewModel.curEndDate.observe(this, calEndObserver)
    }

    //fragment dialog불러오기
    private fun initPerson() {
        binding.textPerson.setOnClickListener {
            val dialog = PersonDialogFragment()
            dialog.show(supportFragmentManager, "PersonDialog")
        }
    }

    //달력 불러오기
    private fun initCalendar() {
        binding.textCalendar.setOnClickListener {

    /*        val datePicker = MaterialDatePicker.Builder.dateRangePicker().build()
            datePicker.show(supportFragmentManager, "DatePicker")*/
            val dialog = CalendarDialogFragment()
            dialog.show(supportFragmentManager, "CalendarDialogFragment")
        }
    }



    //reset
    private fun initReset() {
        binding.apply {
            reset.setOnClickListener {
                textWhereShow.setText("")
                textWhereShow.hint = "어느 지역으로 가시나요?"
                viewModel.curAdult.postValue(0)
                viewModel.curChild.postValue(0)
                viewModel.curAnimal.postValue(0)
            }
        }
    }


/*
    override fun onChanged(t: String?) {
//        val text2 = viewModel.minusValue()
        binding.textWhere.text = viewModel.currentValue.value
    }*/


}