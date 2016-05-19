package org.liangxiaokou.module.album;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import org.liangxiaokou.app.ToolBarActivity;
import org.liangxiaokou.config.Constants;
import org.liangxiaokou.module.R;
import org.liangxiaokou.util.PhotoUtils;
import org.liangxiaokou.util.VolleyLog;
import org.liangxiaokou.widget.dialog.listener.OnOperItemClickL;
import org.liangxiaokou.widget.dialog.widget.NormalListDialog;
import org.mo.netstatus.NetUtils;

import java.io.File;

public class AlbumActivity extends ToolBarActivity {

    private EditText mEditContent;
    private GridView mGridView;
    private GridViewAdapter gridViewAdapter;
    private NormalListDialog photoDialog;//拍照类型

    private static long position;//标识当前是那一张图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        showActionBarBack(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_publish:
                //实现发送的功能
                for (AlbumBean albumBean : gridViewAdapter.getData()) {
                    VolleyLog.e("%s", albumBean.getFilePath());
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean isOverridePendingTransition() {
        return true;
    }

    @Override
    protected PendingTransitionMode getPendingTransitionMode() {
        return PendingTransitionMode.RIGHT;
    }

    @Override
    protected void onNetworkConnected(NetUtils.NetType type) {

    }

    @Override
    protected void onNetworkDisConnected() {

    }

    @Override
    public void initView() {
        mEditContent = (EditText) findViewById(R.id.edit_content);
        mGridView = (GridView) findViewById(R.id.gridView);
        gridViewAdapter = new GridViewAdapter(this);
        mGridView.setAdapter(gridViewAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!gridViewAdapter.getData().get(position).isPick()) {
                    gridViewAdapter.removeData(position);
                } else {
                    AlbumActivity.position = System.currentTimeMillis();
                    photoDialog = new NormalListDialog(AlbumActivity.this, new String[]{"拍照", "相册"});
                    photoDialog.titleBgColor(AlbumActivity.this.getResources().getColor(R.color.system_color));
                    photoDialog.title("请选择");
                    photoDialog.itemTextSize(16);
                    photoDialog.itemPressColor(AlbumActivity.this.getResources().getColor(R.color.line));
                    photoDialog.setOnOperItemClickL(new OnOperItemClickL() {
                        @Override
                        public void onOperItemClick(AdapterView<?> parent, View view, int position, long id) {
                            switch (position) {
                                case 0:
                                    Intent getImageByCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    startActivityForResult(getImageByCamera, Constants.REQUEST_CODE_CAPTURE_CAMERA);
                                    break;
                                case 1:
                                    Intent pick_intent = new Intent(Intent.ACTION_GET_CONTENT);
                                    pick_intent.addCategory(Intent.CATEGORY_OPENABLE);
                                    pick_intent.setType("image/*");//相片类型
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        startActivityForResult(pick_intent, Constants.REQUEST_CODE_PICK_IMAGE_KITKAT);
                                    } else {
                                        startActivityForResult(pick_intent, Constants.REQUEST_CODE_PICK_IMAGE);
                                    }
                                    break;
                                case 2:
                                    break;
                            }
                            photoDialog.dismiss();
                        }
                    });
                    photoDialog.show();
                }
            }
        });
    }

    @Override
    public void initData() {
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        File imageFile = null;
        switch (requestCode) {
            case Constants.REQUEST_CODE_PICK_IMAGE:
            case Constants.REQUEST_CODE_PICK_IMAGE_KITKAT:
                //相册
                if (data == null) {
                    return;
                }
                alertDialog.show();
                imageFile = PhotoUtils.getImageFile(Constants.SAVE_IMAGE_DIR_PATH, Constants.ALBUM_ACTIVITY_PHONE + position + Constants.PNG);
                Uri uri = data.getData();
                PhotoUtils.convertUri(this, uri, imageFile);
                gridViewAdapter.addData(new AlbumBean(imageFile.getPath()));
                break;
            case Constants.REQUEST_CODE_CAPTURE_CAMERA:
                //拍照
                if (data == null) {
                    return;
                } else {
                    Bundle extras = data.getExtras();
                    if (extras != null) {
                        alertDialog.show();
                        imageFile = PhotoUtils.getImageFile(Constants.SAVE_IMAGE_DIR_PATH, Constants.ALBUM_ACTIVITY_PHONE + position + Constants.PNG);
                        Bitmap bm = extras.getParcelable("data");
                        PhotoUtils.saveBitmap(bm, imageFile);
                        gridViewAdapter.addData(new AlbumBean(imageFile.getPath()));
                    }
                }
                break;
        }
        alertDialog.dismiss();
    }


    @Override
    public void PreOnStart() {

    }

    @Override
    public void PreOnResume() {

    }

    @Override
    public void PreOnRestart() {

    }

    @Override
    public void PreOnPause() {

    }

    @Override
    public void PreOnStop() {

    }

    @Override
    public void PreOnDestroy() {

    }

    @Override
    public boolean PreOnKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public void onClick(View v) {

    }

}
