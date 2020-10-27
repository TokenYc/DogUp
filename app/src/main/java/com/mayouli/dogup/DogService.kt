package com.mayouli.dogup

import android.accessibilityservice.AccessibilityService
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.RequiresApi
import androidx.core.os.postDelayed
import java.util.concurrent.Executors

class DogService : AccessibilityService() {

    private val TAG: String = "dog_up"

    private var mLastStateTime: Long = 0
    private var mHasDog: Boolean = false
    private var uiHandler: Handler = Handler(Looper.getMainLooper())

    override fun onInterrupt() {
        Log.d(TAG, "onInterrupt")
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        Log.d(TAG, event?.eventType.toString())
        if (event?.eventType == TYPE_WINDOW_STATE_CHANGED) {
            if (event.source != null) {
                hasDogInfo(event.source)
                if (mHasDog) {
                    val currentTime = System.currentTimeMillis()
                    if (currentTime - mLastStateTime > 1 * 1000) {
                        mLastStateTime = currentTime
                        Log.d(TAG, "开始做任务")
                        performSth(0, event.source)
                    }
                }

//            }
            }
        }
    }


    private fun hasDogInfo(rootNode: AccessibilityNodeInfo) {
        if (rootNode.childCount > 0) {
            for (i in 0 until rootNode.childCount) {
                if (rootNode.getChild(i) != null) {
                    hasDogInfo(rootNode.getChild(i))
                }
            }
        } else {
            if (rootNode.text != null) {
//                Log.d(TAG, "text-->" + rootNode.text)
                if (rootNode.text.toString().contains("11.11 20点兑换分红哦")) {
                    mHasDog = true
                }
            }
        }
    }


    var isFind = false

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun performSth(index: Int, rootNode: AccessibilityNodeInfo) {

        if (rootNode.childCount > 0) {
            for (i in 0 until rootNode.childCount) {
                if (rootNode.getChild(i) != null) {
                    if (!isFind) {
                        performSth(i, rootNode.getChild(i))
                    }
                }
            }
        } else {
            if (rootNode.text != null) {
                if (rootNode.text.contains("逛店8秒并关注") ||
                    rootNode.text.contains("浏览8秒可得") ||
                    rootNode.text.contains("浏览可得")
                ) {
                    var viewTime = 15 * 1000
                    if (rootNode.text.contains("浏览可得")) {
                        viewTime = 4 * 1000
                    }
                    val toFinishNode = rootNode.parent.getChild(index + 1)
                    Log.d(TAG, "find-->" + rootNode.text + "  " + toFinishNode.text)
                    isFind = true
                    toFinishNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    uiHandler.postDelayed(Runnable {
                        isFind = false
                        performGlobalAction(GLOBAL_ACTION_BACK)
                    }, viewTime.toLong())
                    return
                }
            }
        }
    }

}