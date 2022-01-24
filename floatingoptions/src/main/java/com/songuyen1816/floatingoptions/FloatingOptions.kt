package com.songuyen1816.floatingoptions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.RelativeLayout


class FloatingOptions(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {

    companion object {
        const val TAG = "FloatingOptions"
    }

    var prev: Double = 0.0
    var current: Double = 0.0
    var dif: Double = 0.0

    private var buttonDrawable: Drawable
    private var optionsSize: Int = 0
    private var optionsMenu: Menu

    private var screenWidth = Utils.getScreenWidth()
    private var screenHeight = Utils.getScreenHeight()

    private var dX = 0f
    private var dY = 0f

    private var isDown = false
    private var isMoving = false
    private var isShowBelow = false
    private var isShowing = false
    private var isAnimating = false


    private var buttonId = 0

    private lateinit var buttonView: View
    private var optionViews = mutableListOf<View>()
    private var optionsMargin = 0

    private var onOptionSelectListener: OptionSelectedListener? = null

    private var alphaWhenIdle = 0f
    private val gestureDetector: GestureDetector? = null

    init {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FloatingOptions)

        buttonDrawable = typedArray.getDrawable(R.styleable.FloatingOptions_buttonDrawable)!!

        optionsSize =
            typedArray.getDimensionPixelSize(R.styleable.FloatingOptions_optionsSize, 0)

        alphaWhenIdle = typedArray.getFloat(R.styleable.FloatingOptions_alphaWhenIdle, 0f)

        optionsMargin = typedArray.getDimensionPixelSize(R.styleable.FloatingOptions_optionsMargin, 0)

        alpha = alphaWhenIdle

        val menuResId = typedArray.getResourceId(R.styleable.FloatingOptions_menu, 0)
        val p = PopupMenu(getContext(), null)
        optionsMenu = p.menu
        MenuInflater(context).inflate(menuResId, optionsMenu)

        typedArray.recycle()

        addButton()
    }

    private fun addButton() {
        post {
            val buttonImg = ImageView(context)
            val imgLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            buttonImg.layoutParams = imgLayoutParams
            buttonImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
            buttonImg.adjustViewBounds = true
            buttonImg.setImageDrawable(buttonDrawable)

            buttonImg.id = generateViewId()
            buttonId = buttonImg.id

            buttonView = buttonImg
            addView(buttonImg)
        }
    }

    private fun showOptions() {
        post {
            if (y + height < screenHeight / 2) {

                var lastOptionId = 0
                isShowBelow = true

                increaseParentViewHeight()
                configButton()


                for (i in 0 until optionsMenu.size()) {
                    val lp = LayoutParams(optionsSize, optionsSize)

                    if (lastOptionId != 0) {
                        lp.addRule(BELOW, lastOptionId)
                    } else {
                        lp.addRule(BELOW, buttonId)
                    }

                    lp.addRule(CENTER_HORIZONTAL)
                    lp.topMargin = optionsMargin

                    val optionImg = ImageView(context)
                    optionImg.id = generateViewId()
                    lastOptionId = optionImg.id

                    optionImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    optionImg.adjustViewBounds = true
                    optionImg.setImageDrawable(optionsMenu.getItem(i).icon)

                    optionViews.add(optionImg)

                    if (i == 0) {
                        addView(optionImg, lp)
                        displayView(i, optionImg)
                    } else {
                        postDelayed({
                            addView(optionImg, lp)
                            displayView(i, optionImg)
                        }, 100 * i.toLong())
                    }
                    addCallback(i, optionImg)
                }
            } else {
                var lastOptionId = 0
                isShowBelow = false

                increaseParentViewHeight()
                configButton()

                for (i in 0 until optionsMenu.size()) {
                    val optionImg = ImageView(context)

                    val lp = LayoutParams(optionsSize, optionsSize)
                    if (lastOptionId != 0) {
                        lp.addRule(ABOVE, lastOptionId)
                    } else {
                        lp.addRule(ABOVE, buttonId)
                    }
                    lp.addRule(CENTER_HORIZONTAL)
                    lp.bottomMargin = optionsMargin

                    optionImg.id = generateViewId()
                    lastOptionId = optionImg.id

                    optionImg.scaleType = ImageView.ScaleType.CENTER_INSIDE
                    optionImg.adjustViewBounds = true
                    optionImg.setImageDrawable(optionsMenu.getItem(i).icon)

                    //TODO For debug layout
//                    optionImg.setBackgroundResource(R.drawable.background_debug)

                    optionViews.add(optionImg)

                    if (i == 0) {
                        addView(optionImg, lp)
                        displayView(i, optionImg)
                    } else {
                        postDelayed({
                            addView(optionImg, lp)
                            displayView(i, optionImg)
                        }, 100 * i.toLong())
                    }
                    addCallback(i, optionImg)
                }
            }
        }
    }

    private fun addCallback(index: Int, view: View) {
        view.setOnClickListener {
            onOptionSelectListener?.onOptionItemSelected(optionsMenu.getItem(index).itemId)
        }
    }

    private fun displayView(index: Int, view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.option_display_anim)
        view.startAnimation(anim)
        isAnimating = true

        if (index == optionsMenu.size() - 1) {
            postDelayed({
                isAnimating = false
            }, 300)
        }
    }

    private fun hideView(index: Int, view: View) {
        val anim = AnimationUtils.loadAnimation(context, R.anim.option_hide_anim)
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                view.visibility = INVISIBLE
            }

            override fun onAnimationRepeat(p0: Animation?) {
            }
        })
        view.startAnimation(anim)
        isAnimating = true
    }

    fun dismissOptions() {
        if (isShowing && !isAnimating) {
            isShowing = false
            hideOptions()
        }
    }

    private fun hideOptions() {
        optionViews.forEachIndexed { index, view ->
            if (index == 0) {
                hideView(index, view)
            } else {
                postDelayed({
                    hideView(index, view)
                }, 100 * index.toLong())
            }
        }
        postDelayed({
            removeViews(1, optionsMenu.size())
            decreaseParentOptionViewHeight()
            optionViews.clear()
            alpha = alphaWhenIdle
            isAnimating = false
        }, 300 + (optionsMenu.size().toLong() * 80))
    }

    private fun increaseParentViewHeight() {
        if (!isShowBelow) {
            val test = ((optionsSize + optionsMargin) * (optionsMenu.size()))
            y -= test
        }

        val containerLayoutParams = layoutParams
        containerLayoutParams.height = height + ((optionsSize + optionsMargin) * (optionsMenu.size()))
        layoutParams = containerLayoutParams
    }

    private fun decreaseParentOptionViewHeight() {
        if (!isShowBelow) {
            val test = ((optionsSize + optionsMargin) * (optionsMenu.size()))
            y += test
        }

        val containerLayoutParams = layoutParams
        containerLayoutParams.height = height - ((optionsSize + optionsMargin) * (optionsMenu.size()))
        layoutParams = containerLayoutParams
    }


    private fun configButton() {
        val layoutParams = buttonView.layoutParams as LayoutParams
        if (isShowBelow) {
            layoutParams.removeRule(ALIGN_PARENT_BOTTOM)
            layoutParams.addRule(ALIGN_PARENT_TOP)
        } else {
            layoutParams.removeRule(ALIGN_PARENT_TOP)
            layoutParams.addRule(ALIGN_PARENT_BOTTOM)
        }
        buttonView.layoutParams = layoutParams
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (event.action == MotionEvent.ACTION_DOWN) {
            prev = (System.currentTimeMillis()).toDouble();
            return if (!isAnimating) {
                isDown = true
                dX = x - event.rawX
                dY = y - event.rawY
                alpha = 1f
                true
            } else {
                false
            }
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            if (!isAnimating && !isShowing) {
                isMoving = true
                var xMoving = event.rawX + dX
                var yMoving = event.rawY + dY

                if (xMoving < marginLeft) xMoving = marginLeft.toFloat()
                if (yMoving < marginTop) yMoving = marginTop.toFloat()

                if (xMoving + width > screenWidth - marginRight) xMoving =
                    (screenWidth - width - marginRight).toFloat()

                if (yMoving + height > screenHeight - marginBottom) yMoving =
                    (screenWidth - width - marginBottom).toFloat()

                Log.e(TAG, "move: $xMoving / $yMoving")
                x = xMoving
                y = yMoving
            }
            return true
        }
        if (event.action == MotionEvent.ACTION_UP) {
            current = (System.currentTimeMillis()).toDouble();
            dif = current - prev;
            if (dif <= 120) {
                performClick()
                if (isShowing && !isAnimating) {
                    isShowing = false
                    hideOptions()
                } else if (!isShowing && !isAnimating) {
                    isShowing = true
                    showOptions()
                }
            } else {
                if (dif > 120) {
                    if (isMoving && !isAnimating && !isShowing) {
                        if (x + (width / 2) > screenWidth / 2) {
                            animate().translationX((screenWidth - width - (marginRight * 2)).toFloat())
                                .setDuration(200).start()
                        } else {
                            animate().translationX(0f).setDuration(200).start()
                        }
                        isMoving = false
                        alpha = alphaWhenIdle
                        current = 0.0
                        prev = 0.0
                        dif = 0.0
                    }
                }

            }
            isDown = false

//            if (isMoving && !isAnimating && !isShowing) {
//                if (x + (width / 2) > screenWidth / 2) {
//                    animate().translationX((screenWidth - width - (marginRight * 2)).toFloat())
//                            .setDuration(200).start()
//                } else {
//                    animate().translationX(0f).setDuration(200).start()
//                }
//                isMoving = false
//                alpha = alphaWhenIdle
//            }
//            else {
//
//                performClick()
//                if (isShowing && !isAnimating) {
//                    isShowing = false
//                    hideOptions()
//                } else if (!isShowing && !isAnimating) {
//                    isShowing = true
//                    showOptions()
//                }
//            }

            return true
        }

        return false
    }

    fun setOptionSelectedListener(optionSelectListener: OptionSelectedListener) {
        onOptionSelectListener = optionSelectListener
    }

}

inline val View.marginLeft: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.leftMargin ?: 0


inline val View.marginTop: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0


inline val View.marginRight: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.rightMargin ?: 0


inline val View.marginBottom: Int
    get() = (layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0
