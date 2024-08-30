# etranslation-proxy

EU eTranslation Proxy

This application serves as a simple caching proxy / gateway between the [EU eTranslation Service](https://commission.europa.eu/resources-partners/etranslation_en)
and other applications requiring translation of text snippets into one of the official EU languages.

## How it works

The EU eTranslation service is an ansynchronous service requiring an HTTP-callback or ftp / mail service to send the translated snippets to.

* A third-party application sends a translation request via HTTP(S) to this translation proxy
* If the translation is already in the cache/database, the proxy immediately sends the data in the HTTP response body
* If the translation is not readily available, the proxy stores the snippet and sends a translation request to the EU eTranslation service
* The EU eTranslation service sends the translation back to the proxy via the HTTP(S)-callback (or an error response)
* Meanwhile the third-party application can send HTTP requests to the proxy on a regular basis ("is it available yet"), until the translation becomes available
* The proxy will try again if the EU eTranslation service is not available or the maximum amount of requests is reached

## Limitations

The snippet must be a plain-text snippet, with a maximum of 5000 characters.
