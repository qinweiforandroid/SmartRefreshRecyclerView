package com.qw.recyclerview.sample.ui.player

import android.content.res.Configuration
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.activity.OnBackPressedCallback
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.OnRefreshListener
import com.qw.recyclerview.layout.MyLinearLayoutManager
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.sample.databinding.ActivityCctvPlayerBinding
import com.qw.recyclerview.smartrefreshlayout.SmartRecyclerView
import com.qw.recyclerview.template.SmartListCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CctvPlayerActivity : AppCompatActivity() {

    private lateinit var bind: ActivityCctvPlayerBinding
    private lateinit var player: ExoPlayer
    private lateinit var list: SmartListCompat<TvChannel>
    private lateinit var vm: CctvPlayerVM
    private var selectedChannelUrl: String? = null
    private var isFullscreen = false
    private var controlsVisible = true
    private var isTrackingSeekBar = false
    private val uiHandler = Handler(Looper.getMainLooper())
    private val hideControlsRunnable = Runnable {
        if (isFullscreen || player.isPlaying) {
            setControlsVisible(false)
        }
    }
    private val progressRunnable = object : Runnable {
        override fun run() {
            updateProgressUi()
            bind.mClockLabel.text = clockFormatter.format(Date())
            uiHandler.postDelayed(this, 1000L)
        }
    }
    private val clockFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullscreen) {
                updateFullscreenUi(false)
            } else {
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityCctvPlayerBinding.inflate(layoutInflater)
        setContentView(bind.root)
        onBackPressedDispatcher.addCallback(this, backPressedCallback)
        ViewCompat.setOnApplyWindowInsetsListener(bind.root) { view, insets ->
            val topInset = if (isFullscreen) 0 else insets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            view.setPadding(0, topInset, 0, 0)
            insets
        }
        vm = ViewModelProvider(this)[CctvPlayerVM::class.java]
        initPlayer()
        initControls()
        initList()
        observeChannels()
        updateFullscreenUi(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
        bind.mSmartRefreshLayout.autoRefresh()
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    bind.mStatusLabel.text = error.localizedMessage ?: error.errorCodeName
                    showControlsTemporarily()
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayPauseLabel()
                    if (isPlaying) {
                        autoHideControls()
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updateProgressUi()
                }
            })
        }
        bind.mPlayerView.player = player
    }

    private fun initControls() {
        bind.mPlayerContainer.setOnClickListener {
            toggleControls()
        }
        bind.mControlOverlay.setOnClickListener {
            toggleControls()
        }
        bind.mBackBtn.setOnClickListener {
            if (isFullscreen) {
                updateFullscreenUi(false)
            } else {
                finish()
            }
        }
        bind.mFullscreenBtn.setOnClickListener {
            updateFullscreenUi(!isFullscreen)
        }
        bind.mOrientationBtn.setOnClickListener {
            if (isFullscreen) {
                updateFullscreenUi(false)
            } else {
                showControlsTemporarily()
            }
        }
        bind.mPlayPauseBtn.setOnClickListener {
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
            updatePlayPauseLabel()
            showControlsTemporarily()
        }
        bind.mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    val duration = player.duration
                    if (duration > 0) {
                        val seekPosition = duration * progress / 1000L
                        bind.mProgressLabel.text =
                            "${formatDuration(seekPosition)} / ${formatDuration(duration)}"
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isTrackingSeekBar = true
                uiHandler.removeCallbacks(hideControlsRunnable)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                val duration = player.duration
                if (duration > 0 && seekBar != null) {
                    val seekPosition = duration * seekBar.progress / 1000L
                    player.seekTo(seekPosition)
                }
                isTrackingSeekBar = false
                updateProgressUi()
                autoHideControls()
            }
        })
        bind.mFollowBtn.setOnClickListener { showControlsTemporarily() }
    }

    private fun initList() {
        val smart = SmartRecyclerView(bind.mRecyclerView, bind.mSmartRefreshLayout)
        list = object : SmartListCompat<TvChannel>(smart) {
            override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return ChannelHolder(
                    layoutInflater.inflate(R.layout.activity_cctv_player_item, parent, false)
                )
            }
        }
        list.setRefreshEnable(true)
            .setLoadMoreEnable(false)
            .setUpLayoutManager(MyLinearLayoutManager(this))
            .setOnRefreshListener(object : OnRefreshListener {
                override fun onRefresh() {
                    bind.mStatusLabel.text = getString(R.string.player_status_loading)
                    vm.refreshChannels()
                }
            })
    }

    private fun observeChannels() {
        vm.channels.observe(this) { result ->
            bind.mSmartRefreshLayout.finishRefresh()
            val channels = result.getOrElse { TvChannelRepository.fallbackForUi() }
            list.modules.clear()
            list.modules.addAll(channels)
            list.adapter.notifyDataSetChanged()
            if (result.isSuccess) {
                bind.mStatusLabel.text = getString(R.string.player_status_ready, channels.size)
            } else {
                bind.mStatusLabel.text = getString(R.string.player_status_error)
            }
            bind.mAuthorMetaLabel.text = "${channels.size}个直播频道 · 筛选后公开 HLS"
            if (channels.isNotEmpty()) {
                val current = channels.firstOrNull { it.streamUrl == selectedChannelUrl } ?: channels.first()
                playChannel(current)
            } else {
                bind.mNowPlayingLabel.text = getString(R.string.player_empty)
                bind.mOverlayTitleLabel.text = getString(R.string.player_empty)
            }
        }
    }

    private fun playChannel(channel: TvChannel) {
        selectedChannelUrl = channel.streamUrl
        bind.mNowPlayingLabel.text = channel.name
        bind.mOverlayTitleLabel.text = channel.name
        bind.mStatusLabel.text = "${channel.group} · ${channel.sourceLabel}"
        bind.mLiveBadgeLabel.text = channel.group
        player.setMediaItem(MediaItem.fromUri(channel.streamUrl))
        player.prepare()
        player.playWhenReady = true
        updatePlayPauseLabel()
        list.adapter.notifyDataSetChanged()
        showControlsTemporarily()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateFullscreenUi(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
    }

    private fun updateFullscreenUi(fullscreen: Boolean) {
        isFullscreen = fullscreen
        if (fullscreen) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, bind.root).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
            bind.mPlayerContainer.layoutParams = bind.mPlayerContainer.layoutParams.apply {
                height = ViewGroup.LayoutParams.MATCH_PARENT
            }
            bind.mDetailContainer.visibility = View.GONE
            bind.mSmartRefreshLayout.visibility = View.GONE
            bind.mFullscreenIcon.setImageResource(R.drawable.ic_player_fullscreen_exit)
            bind.mOrientationText.visibility = View.VISIBLE
        } else {
            WindowCompat.setDecorFitsSystemWindows(window, true)
            WindowInsetsControllerCompat(window, bind.root).show(WindowInsetsCompat.Type.systemBars())
            bind.mPlayerContainer.layoutParams = bind.mPlayerContainer.layoutParams.apply {
                height = resources.getDimensionPixelSize(R.dimen.player_bili_height)
            }
            bind.mDetailContainer.visibility = View.VISIBLE
            bind.mSmartRefreshLayout.visibility = View.VISIBLE
            bind.mFullscreenIcon.setImageResource(R.drawable.ic_player_fullscreen)
            bind.mOrientationText.visibility = View.GONE
        }
        bind.root.requestApplyInsets()
        bind.mPlayerContainer.requestLayout()
        showControlsTemporarily()
    }

    private fun toggleControls() {
        setControlsVisible(!controlsVisible)
        if (controlsVisible) {
            autoHideControls()
        }
    }

    private fun setControlsVisible(visible: Boolean) {
        controlsVisible = visible
        animateControls(bind.mTopScrim, visible)
        animateControls(bind.mBottomScrim, visible)
        animateControls(bind.mTopControls, visible)
        animateControls(bind.mBottomControls, visible)
    }

    private fun showControlsTemporarily() {
        setControlsVisible(true)
        autoHideControls()
    }

    private fun autoHideControls() {
        uiHandler.removeCallbacks(hideControlsRunnable)
        if (!isTrackingSeekBar) {
            uiHandler.postDelayed(hideControlsRunnable, 3000L)
        }
    }

    private fun updatePlayPauseLabel() {
        val iconRes = if (player.isPlaying) R.drawable.ic_player_pause else R.drawable.ic_player_play
        bind.mPlayPauseIcon.setImageResource(iconRes)
        bind.mPlayPauseIcon.contentDescription =
            getString(if (player.isPlaying) R.string.player_pause else R.string.player_play)
    }

    private fun updateProgressUi() {
        val duration = player.duration.takeIf { it != C.TIME_UNSET && it > 0 } ?: 0L
        val position = player.currentPosition.coerceAtLeast(0L)
        bind.mProgressLabel.text = "${formatDuration(position)}/${formatDuration(duration)}"
        if (!isTrackingSeekBar) {
            bind.mSeekBar.progress = if (duration > 0) ((position * 1000L) / duration).toInt() else 0
        }
    }

    private fun formatDuration(durationMs: Long): String {
        val totalSeconds = durationMs / 1000L
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        }
    }

    override fun onStart() {
        super.onStart()
        uiHandler.post(progressRunnable)
    }

    override fun onStop() {
        uiHandler.removeCallbacks(progressRunnable)
        uiHandler.removeCallbacks(hideControlsRunnable)
        super.onStop()
    }

    private fun animateControls(target: View, visible: Boolean) {
        target.animate().cancel()
        if (visible) {
            if (target.visibility != View.VISIBLE) {
                target.alpha = 0f
                target.visibility = View.VISIBLE
            }
            target.animate()
                .alpha(1f)
                .setDuration(180L)
                .start()
        } else {
            target.animate()
                .alpha(0f)
                .setDuration(180L)
                .withEndAction {
                    target.visibility = View.GONE
                }
                .start()
        }
    }

    override fun onDestroy() {
        bind.mPlayerView.player = null
        player.release()
        super.onDestroy()
    }

    inner class ChannelHolder(itemView: View) : BaseViewHolder(itemView) {
        private val card = itemView.findViewById<LinearLayout>(R.id.mChannelCard)
        private val badge = itemView.findViewById<TextView>(R.id.mChannelBadgeLabel)
        private val title = itemView.findViewById<TextView>(R.id.mChannelNameLabel)
        private val subTitle = itemView.findViewById<TextView>(R.id.mChannelMetaLabel)
        private val tag = itemView.findViewById<TextView>(R.id.mChannelSelectedLabel)

        override fun initData(position: Int) {
            val channel = model as TvChannel
            title.text = channel.name
            subTitle.text = "${channel.group} · ${channel.sourceLabel}"
            badge.text = channel.group.take(4)
            val selected = channel.streamUrl == selectedChannelUrl
            tag.visibility = if (selected) View.VISIBLE else View.GONE
            title.setTypeface(null, if (selected) Typeface.BOLD else Typeface.NORMAL)
            card.setBackgroundResource(
                if (selected) R.drawable.bg_channel_card_selected else R.drawable.bg_channel_card
            )
            itemView.setOnClickListener {
                playChannel(channel)
                if (isFullscreen) {
                    updateFullscreenUi(false)
                }
            }
        }
    }
}
