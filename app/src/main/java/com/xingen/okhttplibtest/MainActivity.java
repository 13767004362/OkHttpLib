package com.xingen.okhttplibtest;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.xingen.okhttplib.NetClient;
import com.xingen.okhttplib.common.listener.FileBlockResponseListener;
import com.xingen.okhttplib.common.listener.ProgressListener;
import com.xingen.okhttplib.common.listener.ResponseListener;
import com.xingen.okhttplibtest.bean.BlockBean;
import com.xingen.okhttplibtest.bean.FileBean;
import com.xingen.okhttplibtest.bean.HttpResult;
import com.xingen.okhttplibtest.bean.Movie;
import com.xingen.okhttplibtest.bean.MovieList;
import com.xingen.okhttplibtest.bean.TokenBean;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    public void initView() {
        findViewById(R.id.main_json_test).setOnClickListener(this);
        findViewById(R.id.main_form_test).setOnClickListener(this);
        findViewById(R.id.main_file_test).setOnClickListener(this);
        findViewById(R.id.main_file_block_test).setOnClickListener(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_json_test: {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("q", "张艺谋");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                NetClient.getInstance().executeJsonRequest("https://api.douban.com/v2/movie/search", jsonObject,
                        new ResponseListener<MovieList<Movie>>() {
                            @Override
                            public void error(Exception e) {
                                Log.i("JsonRequest", "异常结果" + e.getMessage());
                            }

                            @Override
                            public void success(MovieList<Movie> movieMovieList) {
                                Log.i("JsonRequest", "响应结果 " + movieMovieList.toString() + " " + movieMovieList.getSubjects().get(0).getTitle());
                            }
                        });

            }
            break;
            case R.id.main_form_test: {
                for (int i = 0; i < 8; ++i) {
                    Map<String, String> body = new HashMap<>();
                    body.put("appId", "2");
                    body.put("loginName", "testAdmin");
                    body.put("userPass", "123456");
                    NetClient.getInstance().executeFormRequest("http://yanfayi.cn:8889/user/login", body,
                            new ResponseListener<HttpResult<TokenBean>>() {
                                @Override
                                public void error(Exception e) {
                                    Log.i("FormRequest", "异常结果" + e.getMessage());
                                }
                                @Override
                                public void success(HttpResult<TokenBean> tokenBeanHttpResult) {
                                    Log.i("FormRequest", "响应结果 " + tokenBeanHttpResult.toString() + " token是" + tokenBeanHttpResult.data.getToken());
                                }
                            });
                }
                //取消请求

               // NetClient.getInstance().cancelRequests("http://yanfayi.cn:8889/user/login");
            }
            break;
            case R.id.main_file_test: {
                //手机上的文件，这里，本地模拟器上存在的文件
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "baidu.apk";
                //网络的url,这里，本地Eclipse中tomcat服务器上的地址
                String url = "http://192.168.1.16:8080/SSMProject/file/fileUpload";
                NetClient.getInstance().executeSingleFileRequest(url, filePath,
                        new ProgressListener() {
                            @Override
                            public void progress(int progress) {
                                Log.i("SingleFileRequest", "上传进度 " + progress);
                            }
                        },
                        new ResponseListener<FileBean>() {
                            @Override
                            public void error(Exception e) {
                                Log.i("SingleFileRequest", "上传异常 " + e.getMessage());
                            }

                            @Override
                            public void success(FileBean fileBean) {
                                Log.i("SingleFileRequest", "上传成功 " + fileBean.toString());
                            }
                        });
            }
            break;
            case R.id.main_file_block_test: {
                //手机上的文件，这里，本地模拟器上存在的文件
                String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + File.separator + "baihewang.apk";
                //网络的url,这里，本地Eclipse中tomcat服务器上的地址
                String url = "http://192.168.1.16:8080/SSMProject/fileBlock/fileUpload";
                NetClient.getInstance().executeMultiBlockRequest(url, filePath,
                        new ProgressListener() {
                            @Override
                            public void progress(int progress) {
                                Log.i("MultiBlockRequest", "分块上传的进度 " + progress);
                            }
                        }, new FileBlockResponseListener<BlockBean>() {
                            @Override
                            public void error(Exception e) {
                                Log.i("MultiBlockRequest", "分块上传失败 " + e.getMessage());
                            }
                            @Override
                            public void success(BlockBean blockBean) {
                                Log.i("MultiBlockRequest", "分块上传成功 " + blockBean.toString());
                            }
                            @Override
                            public void fileAlreadyUpload(String filePath, BlockBean blockBean) {
                                Log.i("MultiBlockRequest", "文件先前已经上传  "+"路径是："+filePath+" " + blockBean.toString());
                            }
                        });
            }
            break;
        }
    }
}
