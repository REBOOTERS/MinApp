package com.engineer.android.mini.ui.fragments;

import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.engineer.android.mini.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Created on 2020/10/18.
 *
 * @author rookie
 */
public class BaseBottomSheetDialog extends BottomSheetDialogFragment {
    private FrameLayout mBottomSheet;
    public BottomSheetBehavior<FrameLayout> mBehavior;

    @Override
    public void onStart() {
        super.onStart();

        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
        mBottomSheet = dialog.getDelegate()
                .findViewById(com.google.android.material.R.id.design_bottom_sheet);
        if (mBottomSheet != null) {
            CoordinatorLayout.LayoutParams layoutParams =
                    (CoordinatorLayout.LayoutParams) mBottomSheet.getLayoutParams();
            layoutParams.height = getHeight();
            mBottomSheet.setLayoutParams(layoutParams);
            mBehavior = BottomSheetBehavior.from(mBottomSheet);
            mBehavior.setPeekHeight(getHeight());
            // 初始为展开状态
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    @Override
    public int getTheme() {
        return R.style.basedialog_anim_style;
    }

    protected int getHeight() {
        return getResources().getDisplayMetrics().heightPixels;
    }

}
