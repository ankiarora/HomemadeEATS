package com.android.homemadeEATS.views.fragment

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.android.homemadeEATS.R
import com.android.homemadeEATS.repository.common.LoginRepository
import kotlinx.android.synthetic.main.fragment_web_view_test.*
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.*


class WebViewTest : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_web_view_test, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        val url = "https://dabbawala-demo.herokuapp.com/";

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                Log.d(
                    "MyApplication", consoleMessage.message() + " -- From line "
                            + consoleMessage.lineNumber() + " of "
                            + consoleMessage.sourceId()
                )
                return super.onConsoleMessage(consoleMessage)
            }
        }

        val map: MutableMap<String, String> = HashMap()
        map["Authorization"] =
            "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxMWJhYzEwN2Y2NDlmMDAxNjI3OGQxNyIsImlhdCI6MTYyOTIwMzQ4N30.fOlhvRJnsQcFb0hlent5m8d43FxjgQ0QmHnD4M5Xtqo"
        webView.loadUrl(url)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?
            ): WebResourceResponse? {
                return try {
//                    val method = request?.method
//                    val url = request?.url.toString()
//                    Log.d("TAG", "processRequest: $url method $method")
                    val ext = MimeTypeMap.getFileExtensionFromUrl(url)
                    val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)

                    val acToken: String = LoginRepository.accessToken.toString()
                    val okHttpClient = OkHttpClient()
                    val request1: Request =
                        Request.Builder().url(url).addHeader("Authorization", "Bearer $acToken")
                            .build()
                    val response: Response = okHttpClient.newCall(request1).execute()
                    WebResourceResponse(mime, "", response.body?.byteStream())
                } catch (e: Exception) {
                    Log.e("Exception", "exceptionnn:" + e.toString())
                    null
                } catch (e: IOException) {
                    Log.e("Exception", "exceptionnn:" + e.toString())
                    e.printStackTrace()
                    null
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val map: MutableMap<String, String> = HashMap()
                map["Authorization"] =
                    "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxMWJhYzEwN2Y2NDlmMDAxNjI3OGQxNyIsImlhdCI6MTYyOTIwMzQ4N30.fOlhvRJnsQcFb0hlent5m8d43FxjgQ0QmHnD4M5Xtqo"
                view.loadUrl(url)
                return false
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
//                webView.evaluateJavascript("window.localStorage.setItem('x-auth-token','eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxMWJhYzEwN2Y2NDlmMDAxNjI3OGQxNyIsImlhdCI6MTYyOTIwMzQ4N30.fOlhvRJnsQcFb0hlent5m8d43FxjgQ0QmHnD4M5Xtqo');", null);
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        }
    }
}