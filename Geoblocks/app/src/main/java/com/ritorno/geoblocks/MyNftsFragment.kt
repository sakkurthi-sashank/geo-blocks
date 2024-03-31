package com.ritorno.geoblocks

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.ClipboardManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.window.OnBackInvokedDispatcher
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout


class MyNftsFragment : Fragment(R.layout.fragment_code_assist) {

    private val userAgent =
        "Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.5615.135 Mobile Safari/537.36"
    private val chatUrl = "https://geo-block-web.vercel.app/my-nfts"
    private lateinit var webView: WebView
    private lateinit var swipeLayout: SwipeRefreshLayout

    @SuppressLint("JavascriptInterface")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webView = requireActivity().findViewById<WebView>(R.id.webView)
        swipeLayout = requireActivity().findViewById<SwipeRefreshLayout>(R.id.swipeRefreshLayout)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().onBackInvokedDispatcher.registerOnBackInvokedCallback(
                OnBackInvokedDispatcher.PRIORITY_DEFAULT
            ) {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    requireActivity().finish()
                }
            }
        }

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        requireActivity().window.statusBarColor = Color.parseColor("#212a3e")

        webView.settings.userAgentString = userAgent
        webView.settings.domStorageEnabled = true
        webView.settings.javaScriptEnabled = true
        webView.addJavascriptInterface(WebViewInterface(requireActivity()), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                val url = request?.url ?: return false

                if (url.toString().contains(chatUrl)) {
                    return false
                }

                if (webView.url.toString().contains(chatUrl) &&
                    !webView.url.toString().contains("/auth")
                ) {
                    val intent = Intent(Intent.ACTION_VIEW, url)
                    startActivity(intent)
                    return true
                }

                return false
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                swipeLayout.isRefreshing = false
                swipeLayout.isEnabled = !(webView.url.toString().contains(chatUrl) &&
                        !webView.url.toString().contains("/auth"))

                webView.evaluateJavascript(
                    """
                    (() => {
                      navigator.clipboard.writeText = (text) => {
                            Android.copyToClipboard(text);
                            return Promise.resolve();
                        }
                    })();
                    """.trimIndent(),
                    null
                )
            }
        }

        swipeLayout.setOnRefreshListener {
            webView.reload()
        }

        webView.loadUrl(chatUrl)


    }

    private class WebViewInterface(private val context: Context) {
        @JavascriptInterface
        fun copyToClipboard(text: String) {
            val clipboard = context.applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Copied!", text)

            clipboard.text = text
        }
    }
}