package la.shiro.nvramviewer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import la.shiro.nvramviewer.util.NvRam
import la.shiro.nvramviewer.util.NvRamUtil

@SuppressLint("UseSwitchCompatOrMaterialCode")
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var infoTextView: TextView
    private lateinit var flagIndexEditText: EditText
    private lateinit var flagStateSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        infoTextView = findViewById(R.id.tv_info)
        flagIndexEditText = findViewById(R.id.et_flag_index)
        flagStateSwitch = findViewById(R.id.sw_flag_state)
        val dumpNvRamButton = findViewById<Button>(R.id.btn_dumpNvRam)
        val getSnButton = findViewById<Button>(R.id.btn_get_sn)
        val getTestFlagsButton = findViewById<Button>(R.id.btn_get_test_flags)
        val getAllFlagsButton = findViewById<Button>(R.id.btn_get_all_flags)
        val getFlagByIndexButton = findViewById<Button>(R.id.btn_get_flag_by_index)
        val setFlagByIndexButton = findViewById<Button>(R.id.btn_set_flag_by_index)
        val unsetFlagByIndexButton = findViewById<Button>(R.id.btn_unset_flag_by_index)
        val initNvRamButton = findViewById<Button>(R.id.btn_init_nvram)
        dumpNvRamButton.setOnClickListener(this)
        getSnButton.setOnClickListener(this)
        getTestFlagsButton.setOnClickListener(this)
        getAllFlagsButton.setOnClickListener(this)
        getFlagByIndexButton.setOnClickListener(this)
        setFlagByIndexButton.setOnClickListener(this)
        unsetFlagByIndexButton.setOnClickListener(this)
        initNvRamButton.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_dumpNvRam -> {
                infoTextView.text = NvRamUtil.dumpProductInfoNvRam()
            }

            R.id.btn_get_sn -> {
                infoTextView.text = NvRam.getSn()
            }

            R.id.btn_get_test_flags -> {
                infoTextView.text = NvRam.getTestFlagsFromNvRam()
            }

            R.id.btn_get_all_flags -> {
                infoTextView.text = NvRam.getAllFlagsFromNvRam()
            }

            R.id.btn_get_flag_by_index -> {
                if (flagIndexEditText.text.isEmpty()) {
                    infoTextView.text = "Please enter flag index"
                    return
                }
                infoTextView.text =
                    NvRam.getFlagFromNvRam(flagIndexEditText.text.toString().toInt()).toString()
            }

            R.id.btn_set_flag_by_index -> {
                if (flagIndexEditText.text.isEmpty()) {
                    infoTextView.text = "Please enter flag index"
                    return
                }
                infoTextView.text = NvRam.setFlagToNvRam(
                    flagIndexEditText.text.toString().toInt(), flagStateSwitch.isChecked
                ).toString()
            }

            R.id.btn_unset_flag_by_index -> {
                if (flagIndexEditText.text.isEmpty()) {
                    infoTextView.text = "Please enter flag index"
                    return
                }
                infoTextView.text =
                    NvRam.unsetFlagToNvRam(flagIndexEditText.text.toString().toInt()).toString()
            }

            R.id.btn_init_nvram -> {
                infoTextView.text = "NvRam initialized: ${NvRam.initNvRam()}"
            }
        }
    }
}