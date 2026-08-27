# etranslation-proxy

EU eTranslation Proxy

This application serves as a simple caching proxy / gateway between the [EU eTranslation Service](https://commission.europa.eu/resources-partners/etranslation_en)
and other applications requiring translation of text snippets into one of the official EU languages.

## How it works

The EU eTranslation service is an ansynchronous service requiring an HTTP-callback or ftp / mail service to send the translated snippets to.

* A third-party application sends a translation request via HTTP(S) to this translation proxy
* If the translation is already in the cache/database, the proxy does not contact the eTranslation service
* If the translation is not readily available, the proxy stores the snippet and sends a translation request to the EU eTranslation service
* The EU eTranslation service sends the translation back to the proxy via the HTTP(S)-callback (or an error response)
* Meanwhile the third-party application can send HTTP requests to the proxy on a regular basis ("is it available yet"), until the translation becomes available (or the client gives up)
* The proxy will try again if the EU eTranslation service is not available or the maximum amount of requests is reached

## Limitations

The snippet must be a plain-text snippet, with a maximum of 5000 characters.

## Application properties

### Local translation cache database

| Property | Description |
|---|---|
| spring.datasource.driver-class-name=org.postgresql.Driver
| spring.datasource.url | JDBC connection string of the local database |
| spring.datasource.username | User name of local database |
| spring.datasource.password | Password of local database  |

### Request from local client to proxy

| Property | Description |
|---|---|
| request.auth.user | User name to be used by local client |
| request.auth.pass | Password to be used by local client |

### EU eTranslation service

| Property | Description |
|---|---|
|etranslate.url| URL of the EU eTranslation service |
|etranslate.auth.scope| Domain/scope of EU eTranslation authentication |
|etranslate.auth.user| User name for the EU eTranslation service |
|etranslate.auth.pass| Password for the EU eTranslation service |
|etranslate.auth.application | Application for the EU eTranslation service |
|etranslate.url|URL of the EU eTranslation service |
|etranslate.auth.user| User name for the EU eTranslation service |
|etranslate.auth.pass| Password for the EU eTranslation service |
|etranslate.auth.application | Application for the EU eTranslation service |
|etranslate.requests.delay | Delay in seconds between separate HTTP requests to the eTranslation service |
|etranslate.requests.queue.delay | Delay in seconds before the queue is sent as separate requests |
|etranslate.requests.quota.delay | Delay in seconds when eTranslation service has received too many requests |
|etranslate.requests.expire | Time in seconds after which untranslated strings are removed from local queue |

### Callback from EU eTranslation service to proxy

| Property | Description |
|---|---|
| callback.auth.user | User name to be used by eTranslation service |
| callback.auth.pass | Password to be used by eTranslation service |
| callback.error | URL to call in case of error |
| callback.ok | URL to call in case of success |

