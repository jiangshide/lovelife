package com.android.camera.filters;

import android.content.Context;
import com.android.camera.R;

public class BlackFilter extends BaseFilter {


    public BlackFilter(Context c){
        super(c);

    }

    @Override
    public void setPath() {
        path1 = R.raw.base_vertex_shader;
        path2 = R.raw.black_white_shader;
    }



    @Override
    public void onDrawArraysPre() {

    }

    @Override
    public void onDrawArraysAfter() {

    }


}
