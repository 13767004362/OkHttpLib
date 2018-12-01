**介绍**：

> OkHttpLib基于okhttp为传输层，线程池调度的异步通讯网络库。支持Form表单，Json上传文本，单文件上传，超大文件分块断点多线程上传。


**各种Request**：
---

众所周知，网络传输分为文本和文件传输。因此，根据业务封装了以下请求：

- **FormRequest** :
 
    类web前端中的form表单上传文本，content-type为 `application/x-www-form-urlencoded`   ，返回数据采用Gson解析，生成对应的实体bean对象。

- **GsonRequest**:
  
    采用json数据结构上传文本，content-type为`application/json`，返回数据采用Gson解析，生成对应的实体bean对象。

- **SingleFileRequest**

    采用多部分数据结构，上传文件，content-type为`multipart/form-data`，返回数据采用Gson解析，生成对应的实体bean对象，支持进度监听器，和上传结果的回调监听器。
  
    适合体积小的文件上传
  
- **MultiBlockRequest**：

     类似SingleFileRequest,但采用先分块，后分片的方式，数据库记录的方式实现断点多线程并发上传，避免内存溢出，避免重复上传问题。
  
     适合超大文件上传。
  
**使用介绍**
---

**1.在module的build.gradle中，添加依赖:**
```
compile 'com.xingen:okhttplib:1.0.0'
```
除此之外，本项目依赖okhttp和Gson库上进行功能封装,也需依赖:
```
    //OkHttp的依赖
    compile 'com.squareup.okhttp3:okhttp:3.8.0'
    //gson解析库
    compile 'com.google.code.gson:gson:2.2.4'
```

**2. 添加权限**：添加联网权限和读写权限
```
    <uses-permission android:name="android.permission.INTERNET"></uses-permission>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
```

**2. 对框架中配置初始化**：

在Module项目中Application子类中的onCreate()中执行以下代码：
```
@Override
public void onCreate() {
    super.onCreate();
  
  //构建NetConfig配置对象，设置需要的配置。
  
  NetClient.getInstance().initSDK(this,new NetConfig.Builder().setLog(false).builder());
 
  //或者采用默认配置
  NetClient.getInstance().initSDK(this);
        
}
```
接下，来使用各种Request。

**使用JsonRequest请求**：

这里访问douban API中公开的搜索电影的接口，采用json数据结构传递，gson解析该服务器返回的信息。

```JAVA
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


```
**使用FormRequest请求**：

这里访问登入接口，采用form表单传递，gson解析该服务器返回的信息。
```
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

```
这里并发8个网络请求访问，测试多线程并发，调度问题。

**使用SingFileRequest请求**：

上传单文件，进度监听器，结果的回调监听器

```
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

```
这里，采用本地Eclipse中tomcat服务器上运行的后台项目,配合Android端口测试。


**使用MultiBlockRequest请求**

对超大文件进行切割分块，分片，数据库记录，实现断点多线程并发上传。

```
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

```

**如何取消请求**：

方式一：通过url取消：

```
NetClient.getInstance().cancelRequests("http://yanfayi.cn:8889/user/login")
```
方式二： 通过对应的Request取消：
```
//多态，BaseRequest作为SingleFileRequest ，FormRequest，JsonRequest的超类。

BaseRequest request=NetClient.getInstance().executeSingleFileRequest();
BaseRequest request=NetClient.getInstance().executeFormRequest();   
BaseRequest request=NetClient.getInstance().executeJsonRequest(); 

NetClient.getInstance().cancelRequests(request);

//MultiBlockRequest 与其他三种请求不在同一线程池内，作为单独的一块。
MultiBlockRequest multiBlockRequest=NetClient.getInstance().executeMultiBlockRequest();
NetClient.getInstance().cancelRequests(multiBlockRequest);

```
License
-------

    Copyright 2018 HeXinGen.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


