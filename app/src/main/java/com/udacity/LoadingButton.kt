package com.udacity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat.getColor
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var buttonColor = 0
    private var textColor = 0
    private var circleColor = 0
    private var text = "Download"

    private val paintRect = Paint()

    private val paintText = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        textSize = 55.0F
        typeface = Typeface.create("", Typeface.BOLD)
    }

    private val paintCircle = Paint()

    private var fill = 0F
    private var angle = 0F

    private var buttonValue = ValueAnimator()
    private var circleValue = ValueAnimator()

    private var state by Delegates
        .observable<ButtonState>(ButtonState.Completed) { _, _, newState ->
            when (newState) {
                ButtonState.Clicked -> {
                    Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show()
                }

                ButtonState.Loading -> {
                    Toast.makeText(context, "Downloading...", Toast.LENGTH_SHORT).show()
                    text = "We are loading"
                    buttonAnimator()
                    circleAnimator()
                }

                ButtonState.Completed -> {
                    Toast.makeText(context, "Download completed", Toast.LENGTH_SHORT).show()
                    text = "Download"
                    fill = 0F
                    angle = 0F
                    stopAnimations()
                }
            }
        }

    init {
        context.theme.obtainStyledAttributes(attrs, R.styleable.LoadingButton, 0, 0).apply {
            buttonColor = getColor(R.styleable.LoadingButton_buttonColor, 0)
            textColor = getColor(R.styleable.LoadingButton_textColor, 0)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, 0)
        }

        paintRect.color = buttonColor
        paintText.color = textColor
        paintCircle.color = circleColor
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)

        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paintRect.color = this.buttonColor
        canvas!!.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), paintRect)

        canvas.drawText(
            text,
            width.toFloat() / 3,
            height.toFloat() - 55,
            paintText
        )

        if (state == ButtonState.Loading) {
            paintRect.color = getColor(context, R.color.colorPrimaryDark)
            canvas.drawRect(0F, 0F, fill, heightSize.toFloat(), paintRect)

            paintCircle.color = Color.YELLOW

            canvas.drawArc(
                widthSize.toFloat() - 150f,
                heightSize.toFloat() / 2 - 50f,
                widthSize.toFloat()-50f,
                heightSize.toFloat() / 2 + 50f,
                0.0F,
                angle,
                true,
                paintCircle
            )

            canvas.drawText(
                text,
                width.toFloat() / 3,
                height.toFloat() - 55,
                paintText
            )
        }
    }

    @JvmName("setState1")
    fun setState(buttonState: ButtonState) {
        state = buttonState
    }

    private fun buttonAnimator() {
        buttonValue = ValueAnimator.ofFloat(0F, 2500F).apply {
            duration = 2000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { valueAnimator ->
                fill = valueAnimator.animatedValue as Float
                this@LoadingButton.invalidate()
            }

            buttonValue.disableDuringAnimation(this@LoadingButton)
            start()
        }
    }

    private fun circleAnimator() {
        circleValue = ValueAnimator.ofFloat(0F, 360F).apply {
            duration = 1500
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            addUpdateListener { valueAnimator ->
                angle = valueAnimator.animatedValue as Float
                this@LoadingButton.invalidate()
            }

            circleValue.disableDuringAnimation(this@LoadingButton)
            start()
        }
    }

    private fun stopAnimations() {
        buttonValue.end()
        circleValue.end()
        this.invalidate()
    }

    private fun ValueAnimator.disableDuringAnimation(view: View) {
        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator?) {
                view.isEnabled = false
            }
            override fun onAnimationEnd(animation: Animator?) {
                view.isEnabled = true
            }
        })
    }
}