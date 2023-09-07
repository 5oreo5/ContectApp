package com.android.contectapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.android.contectapp.databinding.FragmentAddContactDialogBinding
import java.util.regex.Pattern


class AddContactDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentAddContactDialogBinding

    private val notificationHelper: NotificationHelper by lazy {
        NotificationHelper(requireContext())
    }
    
    override fun onResume() {
        super.onResume()
        //다이얼로그 size
        val windowManager =
            requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        val params: ViewGroup.LayoutParams? = dialog?.window?.attributes
        val deviceWidth = size.x
        val deviceHeight = size.y
        params?.width = (deviceWidth * 0.9).toInt()
        params?.height = (deviceHeight * 0.8).toInt()
        dialog?.window?.attributes = params as WindowManager.LayoutParams
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddContactDialogBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notificationHelper

        binding.addNickName.addTextChangedListener(useTextWatcher(binding.addNickName))

        binding.addSaveBtn.setOnClickListener() {
            // 이름, 번호, 담당 ,메일주소
            val name = binding.addEditName.text.toString()
            val mobile = binding.addMobileEdit.text.toString()
            val special = binding.addSpecialEdit.text.toString()
            val mail = binding.addMailEdit.text.toString()
            val nickname = binding.addCancelBtn.text.toString()
            val newItem = Item(
                0,
                name,
                nickname,
                mobile,
                special,
                mail,
                "새로운 이벤트",
                "새로운 상태",
                false
            )
            NewListRepository.addItem(newItem)

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else if (mobile.isEmpty()) {
                Toast.makeText(requireContext(), "번호를 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else if (special.isEmpty()) {
                Toast.makeText(requireContext(), "담당을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else if (mail.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요!", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
                Toast.makeText(requireContext(), "이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show()
            } else if (!Pattern.matches("^010-\\d{4}-\\d{4}\$", mobile)) {
                Toast.makeText(requireContext(), "올바른 핸드폰 번호가 아닙니다.", Toast.LENGTH_SHORT).show()
            } else if (!Pattern.matches("^[A-Za-z가-힣]+$", special)) {
                Toast.makeText(requireContext(), "한글, 영어 대문자 or 소문자만 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "회원가입 완료!", Toast.LENGTH_SHORT).show()

                val bundle = Bundle()
                bundle.putString("k_name",name)
                arguments?.let {
                    setFragmentResult("r_name",bundle) }

                dismiss()

            }

        }
        binding.addCancelBtn.setOnClickListener() {
            dismiss()
        }

        binding.addNotiOffBtn.setOnClickListener {
            // 알림 취소
            notificationHelper.cancelNotification(1)
            Toast.makeText(requireContext(), "알림을 삭제합니다.", Toast.LENGTH_SHORT).show()
        }

        binding.addNoti10Btn.setOnClickListener {
            val delayMinutes = 10
            val title = "OREO"
            val message = "지금 연락하세요!"
            val uniqueNotificationId = generateUniqueNotificationId() // 고유한 알림 ID 생성
            scheduleSingleAlarmAndNotification(title, message, delayMinutes, uniqueNotificationId)
            Toast.makeText(requireContext(), "10분 뒤 알림이 울립니다.", Toast.LENGTH_SHORT).show()
        }

        binding.addNoti20Btn.setOnClickListener {
            val delayMinutes = 20
            val title = "OREO"
            val message = "지금 연락하세요!"
            val uniqueNotificationId = generateUniqueNotificationId() // 고유한 알림 ID 생성
            scheduleSingleAlarmAndNotification(title, message, delayMinutes, uniqueNotificationId)
            Toast.makeText(requireContext(), "20분 뒤 알림이 울립니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private fun useTextWatcher(editText: EditText): TextWatcher {
        return object : TextWatcher {
            val maxLength = 15
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if ((s?.length ?: 0) > maxLength) {
                    binding.addNickName.error = "최대 $maxLength 글자 까지 입력 가능 합니다."
                } else {
                    binding.addNickName.error = null // 이전에 설정된 오류 지우기.
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun generateUniqueNotificationId(): Int {
        // 현재 시간을 기반으로 고유한 알림 ID 생성
        return System.currentTimeMillis().toInt()
    }

    private fun scheduleSingleAlarmAndNotification(
        title: String,
        content: String,
        delayMinutes: Int,
        notificationId: Int
    ) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), AlertReceiver::class.java)
        intent.action = "SCHEDULED_NOTIFICATION" // "SCHEDULED_NOTIFICATION" 액션 추가

        intent.putExtra("title", title)
        intent.putExtra("content", content)

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 시간을 초 단위로 계산
        val triggerTime = SystemClock.elapsedRealtime() + delayMinutes * 60 * 1000

        alarmManager.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}