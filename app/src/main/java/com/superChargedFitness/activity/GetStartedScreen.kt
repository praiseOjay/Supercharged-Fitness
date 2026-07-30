package com.superChargedFitness.activity

import android.content.Intent
import android.os.Bundle
import com.superChargedFitness.R
import com.superChargedFitness.databinding.ActivityGetStartedScreenBinding
import com.superChargedFitness.utils.ConstantString

class GetStartedScreen : BaseActivity() {

    private lateinit var binding: ActivityGetStartedScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        com.superChargedFitness.utils.Utils.setPref(this,ConstantString.pref_key_is_first_time,false)

        initAction()
    }

    /* todo Default init methods */
    private fun initAction() {
        binding.btnGetStarted.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            /*if(Utils.getPref(this@GetStartedScreen, ConstantString.pref_Key_purchase_status,false)){
                startActivity(Intent(this, HomeActivity::class.java))
            } else{
                startActivity(Intent(this, PurchaseActivity::class.java))
            }*/
            finish()
        }
    }

}
