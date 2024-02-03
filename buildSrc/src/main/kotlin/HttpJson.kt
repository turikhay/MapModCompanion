
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private val client = HttpClient.newHttpClient()
private val objectMapper = ObjectMapper()

private fun jsonBodyHandler(): HttpResponse.BodyHandler<JsonNode> {
    val jsonNodeSubscriber = HttpResponse.BodySubscribers.mapping(HttpResponse.BodySubscribers.ofByteArray()) {
        objectMapper.readTree(it)
    }
    return HttpResponse.BodyHandler { jsonNodeSubscriber }
}

fun jsonRequest(request: HttpRequest): HttpResponse<JsonNode> {
    return client.send(request, jsonBodyHandler())
}