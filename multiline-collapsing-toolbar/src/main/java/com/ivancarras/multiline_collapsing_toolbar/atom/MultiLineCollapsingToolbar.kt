package com.ivancarras.multiline_collapsing_toolbar.atom

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.text.Layout
import android.text.StaticLayout
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.FragmentContainerView
import com.ivancarras.multiline_collapsing_toolbar.R
import kotlinx.android.synthetic.main.multiline_collapsing_toolbar.view.*

class MultiLineCollapsingToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    val fragmentContainerView: FragmentContainerView
        get() = content
    val nestedScrollView: NestedScrollView
        get() = scrollView
    val motionLayoutView: MotionLayout
        get() = motionLayout

    var navigationIconOnClick: (() -> Unit)? = null

    private var isTransitionEnabled: Boolean
        get() = motionLayout.getTransition(R.id.collapsingTransition).isEnabled
        set(value) {
            motionLayout.getTransition(R.id.collapsingTransition).setEnable(value)
        }

    init {
        View.inflate(context, R.layout.multiline_collapsing_toolbar, this)
        retrieveStyledAttributes(attrs)
        navigationIcon.setOnClickListener {
            navigationIconOnClick?.invoke()
        }
    }

    private fun retrieveStyledAttributes(attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.MultiLineCollapsingToolbar)
        setNavigationIcon(newIcon = typedArray.navigationIcon)
        setTitle(newTitle = typedArray.title)
        typedArray.recycle()
    }

    /*
        The transition is only enabled if the scroll view can scroll because the container view is bigger than the scroll
     */
    private fun shouldBeTheTransitionEnabled() =
        fragmentContainerView.height > nestedScrollView.height

    private fun handleTransitionAnimation() {
        isTransitionEnabled = shouldBeTheTransitionEnabled()
    }

    fun setNavigationIcon(newIcon: Drawable?) {
        navigationIcon.setImageDrawable(newIcon)
    }

    fun setTitle(newTitle: String?) {
        title.text = newTitle
        title.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                title.viewTreeObserver.removeOnPreDrawListener(this)
                recalculateTitleConstraints()
                handleTransitionAnimation()
                return true
            }
        })
    }

/*
   We have to recalculate the expanded height of the view because if we use wrap_content the constraints
   provoke a flickering movement in the text when you scroll , so it's needed define the expanded text height
 */

    private fun recalculateTitleConstraints() {
        val constraintToModify = motionLayout.getConstraintSet(R.id.expanded)

        constraintToModify.constrainHeight(
            R.id.title,
            title.getPreRenderedHeight()
        )
        constraintToModify.applyTo(motionLayout)
    }

    private fun TextView.getPreRenderedHeight(): Int {
        val parentWidth =
            (parent as View).width - (title.layoutParams as LayoutParams).marginStart - (title.layoutParams as LayoutParams).marginEnd

        val layout = getPreRendererLayout(parentWidth)

        return layout.height
    }

    private fun TextView.getPreRendererLayout(parentWidth: Int): StaticLayout =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            StaticLayout.Builder.obtain(text, 0, text.length, title.paint, parentWidth)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(0.0f, 1.0f)
                .setIncludePad(false)
                .build()
        } else {
            @Suppress("DEPRECATION")
            StaticLayout(
                text,
                title.paint,
                parentWidth,
                Layout.Alignment.ALIGN_NORMAL,
                1.0f,
                0.0f,
                false
            )
        }

    override fun onSaveInstanceState(): Parcelable? =
        Bundle().apply {
            putFloat(TRANSITION_PROGRESS, motionLayoutView.progress)
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
        }


    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            restoreMotionLayoutProgress(state)
            super.onRestoreInstanceState(state.getParcelable(SUPER_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    private fun restoreMotionLayoutProgress(state: Bundle) {
        title.post {
            motionLayoutView.progress = state.getFloat(TRANSITION_PROGRESS)
        }
    }

    private val TypedArray.title: String?
        get() = getString(R.styleable.MultiLineCollapsingToolbar_title)
    private val TypedArray.navigationIcon: Drawable?
        get() = getDrawable(R.styleable.MultiLineCollapsingToolbar_onBackIcon)
            ?: AppCompatResources.getDrawable(context, R.drawable.ic_back_arrow)

    companion object {
        private const val TRANSITION_PROGRESS = "TRANSITION_PROGRESS"
        private const val SUPER_STATE = "SUPER_STATE"
    }
}