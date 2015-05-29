/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 Nir Hartmann
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fastbootmobile.encore.app.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

import com.fastbootmobile.encore.app.R;

public class ParallaxListViewHelper implements OnScrollListener {

    private static final float DEFAULT_PARALLAX_FACTOR = 1.9F;
    private static final boolean DEFAULT_IS_CIRCULAR = false;
    private float parallaxFactor = DEFAULT_PARALLAX_FACTOR;
    private ParallaxedView parallaxedView;
    private boolean isCircular;
    private OnScrollListener listener = null;
    private ListView listView;

    protected ParallaxListViewHelper(Context context, AttributeSet attrs, ListView listView) {
        init(context, attrs, listView);
    }

    protected void init(Context context, AttributeSet attrs, ListView listView) {
        this.listView = listView;
        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.ParallaxScroll);
        this.parallaxFactor = typeArray.getFloat(R.styleable.ParallaxScroll_parallax_factor, DEFAULT_PARALLAX_FACTOR);
        this.isCircular = typeArray.getBoolean(R.styleable.ParallaxScroll_circular_parallax, DEFAULT_IS_CIRCULAR);
        typeArray.recycle();
    }

    protected void setOnScrollListener(OnScrollListener l) {
        this.listener = l;
    }

    protected void addParallaxedHeaderView(View v) {
        addParallaxedView(v);
    }

    protected void addParallaxedHeaderView(View v, Object data, boolean isSelectable) {
        addParallaxedView(v);
    }

    protected void addParallaxedView(View v) {
        this.parallaxedView = new ListViewParallaxedItem(v);
    }

    protected void parallaxScroll() {
        if (isCircular)
            circularParallax();
        else
            headerParallax();
    }

    private void circularParallax() {
        if (listView.getChildCount() > 0) {
            int top = -listView.getChildAt(0).getTop();
            float factor = parallaxFactor;
            fillParallaxedViews();
            parallaxedView.setOffset((float)top / factor);
        }
    }

    private void headerParallax() {
        if (parallaxedView != null) {
            if (listView.getChildCount() > 0) {
                int top = -listView.getChildAt(0).getTop();
                float factor = parallaxFactor;
                parallaxedView.setOffset((float)top / factor);
            }
        }
    }

    private void fillParallaxedViews() {
        if (parallaxedView == null || parallaxedView.is(listView.getChildAt(0)) == false) {
            if (parallaxedView != null) {
                parallaxedView.setOffset(0);
                parallaxedView.setView(listView.getChildAt(0));
            } else {
                parallaxedView = new ListViewParallaxedItem(listView.getChildAt(0));
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        parallaxScroll();
        if (this.listener != null)
            this.listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (this.listener != null)
            this.listener.onScrollStateChanged(view, scrollState);
    }

    protected class ListViewParallaxedItem extends ParallaxedView {

        public ListViewParallaxedItem(View view) {
            super(view);
        }

        @Override
        protected void translatePreICS(View view, float offset) {
            TranslateAnimation ta = new TranslateAnimation(0, 0, offset, offset);
            ta.setDuration(0);
            ta.setFillAfter(true);
            view.setAnimation(ta);
            ta.start();
        }
    }
}