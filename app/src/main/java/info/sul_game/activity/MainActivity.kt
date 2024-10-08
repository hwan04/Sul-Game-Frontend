package info.sul_game.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import info.sul_game.R
import info.sul_game.databinding.ActivityMainBinding
import info.sul_game.recyclerview.DrinkingGameMainAdapter
import info.sul_game.recyclerview.DrinkingGameMainItem
import info.sul_game.recyclerview.IntroMainAdapter
import info.sul_game.recyclerview.IntroMainItem
import info.sul_game.recyclerview.LatestFeedMainAdapter
import info.sul_game.recyclerview.LatestFeedMainDecoration
import info.sul_game.recyclerview.LatestFeedMainItem
import info.sul_game.recyclerview.LiveChartMainAdapter
import info.sul_game.recyclerview.LiveChartMainItem
import info.sul_game.recyclerview.UserRankingMainAdapter
import info.sul_game.recyclerview.UserRankingMainItem
import info.sul_game.utils.TokenUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var behavior: BottomSheetBehavior<ConstraintLayout>
    private val handler = Handler(Looper.getMainLooper())
    private var userInteractionTimeout: Long = 2000 // 5초 후에 FAB 숨김

    private val hideFabRunnable = Runnable {
        binding.expandableFab.hide()  // 일정 시간 후 FloatingActionButton 숨기기
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        val firstTime = prefs.getBoolean("firstTime", true)

        if (firstTime) {
            startActivity(Intent(this, WelcomeGuideActivity::class.java))
            finish()
        }

        findViewById<View>(android.R.id.content).setOnTouchListener { _, _ ->
            resetFabTimer() // 사용자 입력 발생 시 타이머 리셋
            false
        }

        persistentBottomSheetEvent()
        recyclerMain()
         officialMove()

        startFabTimer()

        if(TokenUtil().getRefreshToken(this@MainActivity).isNullOrBlank()) {
            binding.tvMypageLoginMain.text = "마이페이지 서비스는\n로그인이 필요해요!"
            binding.tvMypageLoginMain.textSize = 18f
        } else {
            binding.tvMypageLoginMain.text = "마이 페이지"
            binding.tvMypageLoginMain.textSize = 32f
        }

        // Firebase 초기화
        FirebaseApp.initializeApp(this)

        // 그 후에 FCM 토큰 관련 작업 수행
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("술겜위키", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            val token = task.result
            Log.d("술겜위키", "토큰 이름 : $token")
            Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

        }
        createMove()

    }
       
    
    private fun officialMove(){
        binding.btnOfficialMain.setOnClickListener {
            val intent = Intent(this, OfficialGameListActivity::class.java)
            startActivity(intent)}}
    private fun createMove(){
        binding.fabMakeMain.setOnClickListener{
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }
    }


    private fun recyclerMain() {
        recentRecyclerMain()
        popularGameRecyclerMain()
        liveChartRecyclerMain()
        shareIntroRecyclerMain()
        hotGameRecyclerMain()
        userRankingRecyclerMain()
    }

    // 최신 게시물의 리사이클러뷰
    private fun recentRecyclerMain() {
        val latestFeedMainItemCreationData = arrayListOf<LatestFeedMainItem>(
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 창작", R.drawable.ic_launcher_background, "구해조", 30),
        )

        val latestFeedMainItemIntroData = arrayListOf<LatestFeedMainItem>(
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
            LatestFeedMainItem(R.drawable.ic_launcher_foreground , "어목조동 인트로", R.drawable.ic_launcher_background, "구해조", 30),
        )

        binding.rvRecentMain.adapter = LatestFeedMainAdapter(latestFeedMainItemCreationData)
        binding.rvRecentMain.addItemDecoration(LatestFeedMainDecoration())
        binding.rvRecentMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        binding.tvRecentCreationMain.setOnClickListener {
            binding.tvRecentCreationMain.setTextColor(getColor(R.color.main_color))
            binding.tvRecentIntroMain.setTextColor(
                android.graphics.Color.parseColor(
                    "#8C8C8C"
                )
            )

            binding.rvRecentMain.adapter = LatestFeedMainAdapter(latestFeedMainItemCreationData)
            binding.rvRecentMain.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }

        binding.tvRecentIntroMain.setOnClickListener {
            binding.tvRecentCreationMain.setTextColor(
                android.graphics.Color.parseColor(
                    "#8C8C8C"
                )
            )
            binding.tvRecentIntroMain.setTextColor(getColor(R.color.main_color))

            binding.rvRecentMain.adapter = LatestFeedMainAdapter(latestFeedMainItemIntroData)
            binding.rvRecentMain.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun popularGameRecyclerMain() {
        val popularGameData = arrayListOf<DrinkingGameMainItem>(
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니",
                "하늘에서 내려와버린 토끼",
                30
            )
        )

        binding.rvPopularMain.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(
                this@MainActivity,
                3,
                GridLayoutManager.HORIZONTAL,
                false
            )
            adapter = DrinkingGameMainAdapter(popularGameData)
        }
    }

    private fun liveChartRecyclerMain() {
        val liveChartMainItemCreationData = arrayListOf<LiveChartMainItem>(
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 창작",
                "하늘에서 내려와버린 토끼",
                30
            )
        )

        val liveChartMainItemIntroData = arrayListOf<LiveChartMainItem>(
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 인트로",
                "하늘에서 내려와버린 토끼",
                30
            )
        )

        val liveChartMainItemPopularData = arrayListOf<LiveChartMainItem>(
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            ),
            LiveChartMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 국룰",
                "하늘에서 내려와버린 토끼",
                30
            )
        )

        binding.rvLiveMain.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(
                this@MainActivity,
                3,
                GridLayoutManager.HORIZONTAL,
                false
            )
            adapter = LiveChartMainAdapter(liveChartMainItemCreationData)
        }

        // 각 Chip에 클릭 리스너 추가 (선택 상태 유지 보장)
        binding.cpCreationMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpCreationMain)
            binding.rvLiveMain.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(
                    this@MainActivity,
                    3,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                adapter = LiveChartMainAdapter(liveChartMainItemCreationData)
            }
        }
        binding.cpIntroMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpIntroMain)
            binding.rvLiveMain.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(
                    this@MainActivity,
                    3,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                adapter = LiveChartMainAdapter(liveChartMainItemIntroData)
            }
        }
        binding.cpPopularMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpPopularMain)
            binding.rvLiveMain.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(
                    this@MainActivity,
                    3,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                adapter = LiveChartMainAdapter(liveChartMainItemPopularData)
            }
        }
    }

    private fun shareIntroRecyclerMain() {
        val shareIntroRecentData = arrayListOf<IntroMainItem>(
            IntroMainItem("어목조동 최신", "자연과 함께하는 술게임", "구해조", 30),
            IntroMainItem("딸기당근수박참외 찍고 최신", "지목이 더해진 과일게임", "구해조", 30),
            IntroMainItem("딸기당근수박참외 리버스 최신", "거꾸로 말하기 도전!", "구해조", 30)
        )

        val shareIntroHeartData = arrayListOf<IntroMainItem>(
            IntroMainItem("어목조동 좋아요", "자연과 함께하는 술게임", "구해조", 30),
            IntroMainItem("딸기당근수박참외 찍고 좋아요", "지목이 더해진 과일게임", "구해조", 30),
            IntroMainItem("딸기당근수박참외 리버스 좋아요", "거꾸로 말하기 도전!", "구해조", 30)
        )

        val shareIntroViewData = arrayListOf<IntroMainItem>(
            IntroMainItem("어목조동 조회수", "자연과 함께하는 술게임", "구해조", 30),
            IntroMainItem("딸기당근수박참외 찍고 조회수", "지목이 더해진 과일게임", "구해조", 30),
            IntroMainItem("딸기당근수박참외 리버스 조회수", "거꾸로 말하기 도전!", "구해조", 30)
        )

        binding.rvShareIntroMain.adapter = IntroMainAdapter(shareIntroRecentData)
        binding.rvShareIntroMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        // 각 Chip에 클릭 리스너 추가 (선택 상태 유지 보장)
        binding.cpRecentMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpRecentMain)
            binding.rvShareIntroMain.adapter = IntroMainAdapter(shareIntroRecentData)
            binding.rvShareIntroMain.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        binding.cpHeartMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpHeartMain)
            binding.rvShareIntroMain.adapter = IntroMainAdapter(shareIntroHeartData)
            binding.rvShareIntroMain.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
        binding.cpViewMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpViewMain)
            binding.rvShareIntroMain.adapter = IntroMainAdapter(shareIntroViewData)
            binding.rvShareIntroMain.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun hotGameRecyclerMain() {
        val hotWeeklyGameData = arrayListOf<DrinkingGameMainItem>(
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 주간",
                "하늘에서 내려와버린 토끼",
                30
            )
        )

        val hotDailyGameData = arrayListOf<DrinkingGameMainItem>(
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            ),
            DrinkingGameMainItem(
                R.drawable.ic_launcher_background,
                "바니바니 일간",
                "하늘에서 내려와버린 토끼",
                30
            )
        )

        binding.rvHotMain.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(
                this@MainActivity,
                3,
                GridLayoutManager.HORIZONTAL,
                false
            )
            adapter = DrinkingGameMainAdapter(hotWeeklyGameData)
        }

        // 각 Chip에 클릭 리스너 추가 (선택 상태 유지 보장)
        binding.cpWeeklyMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpWeeklyMain)
            binding.rvHotMain.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(
                    this@MainActivity,
                    3,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                adapter = DrinkingGameMainAdapter(hotWeeklyGameData)
            }
        }
        binding.cpDailyMain.setOnClickListener { v ->
            ensureOneSelected(binding.cpDailyMain)
            binding.rvHotMain.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(
                    this@MainActivity,
                    3,
                    GridLayoutManager.HORIZONTAL,
                    false
                )
                adapter = DrinkingGameMainAdapter(hotDailyGameData)
            }
        }
    }

    private fun userRankingRecyclerMain() {
        val userRankingData = arrayListOf<UserRankingMainItem>(
            UserRankingMainItem(R.drawable.ic_launcher_background, "UP", 5, "닉네임1", 10000),
            UserRankingMainItem(R.drawable.ic_launcher_background,"DOWN", 4, "닉네임2", 5000),
            UserRankingMainItem(R.drawable.ic_launcher_background,"SAME", 0, "닉네임3", 3000),
            UserRankingMainItem(R.drawable.ic_launcher_background,"NEW", 0, "닉네임4", 1000)
        )

        binding.rvUserRankingMain.adapter = UserRankingMainAdapter(userRankingData)
        binding.rvUserRankingMain.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun ensureOneSelected(selectedChip: Chip) {
        if (!selectedChip.isChecked) {
            // 선택된 Chip을 선택 해제한 경우
            binding.cgShareIntroChipsMain.clearCheck()
            selectedChip.isChecked = true
        }
    }

    // 화면 초기화 시 호출되는 bottomSheet에서의 이벤트 처리를 위한 함수
    private fun persistentBottomSheetEvent() {

        binding.searchviewMain.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }

        behavior = BottomSheetBehavior.from(binding.bottomSheetMain)
        behavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    // 완전히 펼쳐졌을 때
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        if(!TokenUtil().getRefreshToken(this@MainActivity).isNullOrBlank()){
                            val intent = Intent(this@MainActivity, MyPageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                            startActivity(intent)
                        } else {
                            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            Toast.makeText(this@MainActivity, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }

                    // 드래깅 되고있을 때
                    BottomSheetBehavior.STATE_DRAGGING -> {
                        binding.backgroundDimMain.visibility = View.VISIBLE
                        Log.d("술겜위키", "refreshToken : ${TokenUtil().getRefreshToken(this@MainActivity)}")
                        if(TokenUtil().getRefreshToken(this@MainActivity).isNullOrBlank()){
                            startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                            finish()
                        }
                    }

                    // 접혀있을 때
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.backgroundDimMain.visibility = View.GONE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.backgroundDimMain.alpha = slideOffset
            }
        })
    }

    override fun onResume() {
        super.onResume()
        behavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun startFabTimer() {
        // 일정 시간 후 FloatingActionButton 숨김
        handler.postDelayed(hideFabRunnable, userInteractionTimeout)
    }

    private fun resetFabTimer() {
        // 타이머 리셋
        handler.removeCallbacks(hideFabRunnable)
        handler.postDelayed(hideFabRunnable, userInteractionTimeout)
        binding.expandableFab.show() // 다시 사용자가 입력하면 FAB 표시
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        resetFabTimer() // 어떤 사용자 입력이 발생해도 타이머를 리셋
    }

    override fun onDestroy() {
        super.onDestroy()
        // 메모리 누수를 방지하기 위해 Handler를 해제
        handler.removeCallbacks(hideFabRunnable)
    }}

