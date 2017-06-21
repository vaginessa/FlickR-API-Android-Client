package com.ntwired3.flickrclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidviewhover.BlurLayout;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.ntwired3.flickrclient.R;
import com.ntwired3.flickrclient.models.FlickrImage;
import com.ntwired3.flickrclient.utils.ImageHandler;

import java.io.File;
import java.util.List;

/**
 * Created by user on 20/06/2017.
 */

public class ImageAdapter extends BaseAdapter {

    private static final long ANIM_DURATION = 550;
    private static final long ANIM_500 = 500;
    private static final long ANIM_250 = 250;
    private final Context mContext;
    private final List<FlickrImage> mData;

    public ImageAdapter(Context mContext, List<FlickrImage> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public FlickrImage getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.image, null);
        }
        // find the image view
        final ImageView iv = (ImageView) convertView.findViewById(R.id.image);
        final TextView title = (TextView) convertView.findViewById(R.id.title);
        final TextView date_published = (TextView) convertView.findViewById(R.id.date_published);
        BlurLayout sampleLayout = (BlurLayout) convertView.findViewById(R.id.blurContainer);


        //Based on the assignment there are only 2 items added as 'meta-data' which are Title & Date_published, Didn't think we need more
        // fill the view info
        FlickrImage image = getItem(position);
        title.setText(image.getTitle());
        date_published.setText(image.getPublished().toString());
        Ion.with(iv)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .load(image.getImageURL());

        setupBlurLayout(sampleLayout, image.getImageURL());

        return convertView;
    }

    /**
     * Is a method for sub-menu when clicking on an image
     *
     * @param sampleLayout
     */
    private void setupBlurLayout(BlurLayout sampleLayout, final String url) {
        View hover = LayoutInflater.from(mContext).inflate(R.layout.hover, null);
        sampleLayout.setHoverView(hover);
        sampleLayout.enableZoomBackground(true);
        sampleLayout.setBlurDuration(1200);

        //appear animation.
        sampleLayout.addChildAppearAnimator(hover, R.id.btn_save, Techniques.FlipInX, ANIM_DURATION, 0);
        sampleLayout.addChildAppearAnimator(hover, R.id.btn_open, Techniques.FlipInX, ANIM_DURATION, ANIM_250);
        sampleLayout.addChildAppearAnimator(hover, R.id.btn_share, Techniques.FlipInX, ANIM_DURATION, ANIM_500);

        //disappear animation.
        sampleLayout.addChildDisappearAnimator(hover, R.id.btn_save, Techniques.FlipOutX, ANIM_DURATION, ANIM_500);
        sampleLayout.addChildDisappearAnimator(hover, R.id.btn_open, Techniques.FlipOutX, ANIM_DURATION, ANIM_250);
        sampleLayout.addChildDisappearAnimator(hover, R.id.btn_share, Techniques.FlipOutX, ANIM_DURATION, 0);

        //setup clicks
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                YoYo.with(Techniques.Tada)
                        .duration(ANIM_DURATION)
                        .playOn(v);

                if (v.getId() == R.id.btn_open) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    mContext.startActivity(intent);
                } else {
                    //Saves the image
                    File image = ImageHandler.prepareImageFile(url);
                    Ion.with(mContext)
                            .load(url)
                            .write(image)
                            .setCallback(new FutureCallback<File>() {
                                @Override
                                public void onCompleted(Exception e, File file) {
                                    if (e != null)
                                        e.printStackTrace();
                                    else {
                                        if (file != null && file.exists()) {
                                            //If this is a request for Gallery then save to Gallery, Otherwise we email the image
                                            if (v.getId() == R.id.btn_save)
                                                ImageHandler.addImageToGallery(file.getAbsolutePath(), mContext);
                                            else
                                                ImageHandler.emailImage(mContext, file.getAbsolutePath());
                                        }
                                        Toast.makeText(mContext, mContext.getString(R.string.imageSaved), Toast.LENGTH_LONG);
                                    }
                                }
                            });
                }
            }
        };
        hover.findViewById(R.id.btn_open).setOnClickListener(listener);
        hover.findViewById(R.id.btn_save).setOnClickListener(listener);
        hover.findViewById(R.id.btn_share).setOnClickListener(listener);
    }

    /**
     * Loads the data to the adapter
     *
     * @param images
     */
    public void loadData(List<FlickrImage> images) {
        this.mData.addAll(images);
        notifyDataSetChanged();
    }

    /**
     * Removes all data from the adapter but it remains in the database
     */
    public void clear() {
        this.mData.clear();
        notifyDataSetChanged();
    }
}