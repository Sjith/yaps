/**
 *
 */
package com.ogunwale.android.app.yaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author ogunwale
 *
 */
public class PhotoThumbnailAdapter extends BaseAdapter {

    private Context mContext;

    /**
     *
     */
    public PhotoThumbnailAdapter(Context context) {
        mContext = context;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return 50;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroups)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if (convertView == null) {
            LayoutInflater li = LayoutInflater.from(mContext);
            view = li.inflate(R.layout.layout_photo_thumbnail, null);
        } else {
            view = convertView;
        }

        ImageView image = (ImageView) view.findViewById(R.id.thumbnail_image);
        image.setImageResource(R.drawable.ic_launcher);

        TextView description = (TextView) view.findViewById(R.id.thumbnail_description);
        description.setText("qwerty");
        // description.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        TextView count = (TextView) view.findViewById(R.id.thumbnail_count);
        count.setText(String.valueOf(position));
        // count.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

        return view;
    }

}
