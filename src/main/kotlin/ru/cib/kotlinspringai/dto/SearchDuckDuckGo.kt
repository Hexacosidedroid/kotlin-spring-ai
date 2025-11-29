package ru.cib.kotlinspringai.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class SearchDuckDuckGo(
    @JsonProperty("Abstract") val abstract: String? = null,
    @JsonProperty("AbstractSource") val abstractSource: String? = null,
    @JsonProperty("AbstractText") val abstractText: String? = null,
    @JsonProperty("AbstractURL") val abstractURL: String? = null,
    @JsonProperty("Answer") val answer: String? = null,
    @JsonProperty("AnswerType") val answerType: String? = null,
    @JsonProperty("Definition") val definition: String? = null,
    @JsonProperty("DefinitionSource") val definitionSource: String? = null,
    @JsonProperty("DefinitionURL") val definitionURL: String? = null,
    @JsonProperty("Entity") val entity: String? = null,
    @JsonProperty("Heading") val heading: String? = null,
    @JsonProperty("Image") val image: String? = null,
    @JsonProperty("ImageHeight") val imageHeight: Int? = null,
    @JsonProperty("ImageIsLogo") val imageIsLogo: Int? = null,
    @JsonProperty("ImageWidth") val imageWidth: Int? = null,
    @JsonProperty("Redirect") val redirect: String? = null,
    @JsonProperty("RelatedTopics") val relatedTopics: List<RelatedTopic> = emptyList(),
    @JsonProperty("Results") val results: List<SearchResult> = emptyList(),
    @JsonProperty("Type") val type: String? = null,
    @JsonProperty("meta") val meta: Meta? = null,
    @JsonProperty("Infobox") val infobox: Any? = null,
    @JsonProperty("OfficialDomain") val officialDomain: String? = null,
    @JsonProperty("OfficialWebsite") val officialWebsite: String? = null
)

data class Infobox(
    @JsonProperty("content") val content: List<InfoboxContent>? = null,
    @JsonProperty("meta") val meta: List<InfoboxMeta>? = null
)

data class InfoboxContent(
    @JsonProperty("data_type") val dataType: String? = null,
    @JsonProperty("label") val label: String? = null,
    @JsonProperty("sort_order") val sortOrder: String? = null,
    @JsonProperty("value") val value: Any? = null, // Use Any because value can be String or Object
    @JsonProperty("wiki_order") val wikiOrder: Int? = null
)

data class InfoboxMeta(
    @JsonProperty("data_type") val dataType: String? = null,
    @JsonProperty("label") val label: String? = null,
    @JsonProperty("value") val value: String? = null
)

data class RelatedTopic(
    @JsonProperty("FirstURL") val firstURL: String? = null,
    @JsonProperty("Icon") val icon: Icon? = null,
    @JsonProperty("Result") val result: String? = null,
    @JsonProperty("Text") val text: String? = null,
    @JsonProperty("Name") val name: String? = null,
    @JsonProperty("Topics") val topics: List<RelatedTopic>? = null
)

data class SearchResult(
    @JsonProperty("FirstURL") val firstURL: String? = null,
    @JsonProperty("Icon") val icon: Icon? = null,
    @JsonProperty("Result") val result: String? = null,
    @JsonProperty("Text") val text: String? = null
)

data class Icon(
    @JsonProperty("Height") val height: String? = null,
    @JsonProperty("URL") val url: String? = null,
    @JsonProperty("Width") val width: String? = null
)

data class Meta(
    @JsonProperty("attribution") val attribution: Any? = null,
    @JsonProperty("blockgroup") val blockgroup: Any? = null,
    @JsonProperty("created_date") val createdDate: Any? = null,
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("designer") val designer: Any? = null,
    @JsonProperty("dev_date") val devDate: Any? = null,
    @JsonProperty("dev_milestone") val devMilestone: String? = null,
    @JsonProperty("developer") val developer: List<Developer> = emptyList(),
    @JsonProperty("example_query") val exampleQuery: String? = null,
    @JsonProperty("id") val id: String? = null,
    @JsonProperty("is_stackexchange") val isStackexchange: Any? = null,
    @JsonProperty("js_callback_name") val jsCallbackName: String? = null,
    @JsonProperty("live_date") val liveDate: Any? = null,
    @JsonProperty("maintainer") val maintainer: Maintainer? = null,
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("perl_module") val perlModule: String? = null,
    @JsonProperty("producer") val producer: Any? = null,
    @JsonProperty("production_state") val productionState: String? = null,
    @JsonProperty("repo") val repo: String? = null,
    @JsonProperty("signal_from") val signalFrom: String? = null,
    @JsonProperty("src_domain") val srcDomain: String? = null,
    @JsonProperty("src_id") val srcId: Int? = null,
    @JsonProperty("src_name") val srcName: String? = null,
    @JsonProperty("src_options") val srcOptions: SrcOptions? = null,
    @JsonProperty("src_url") val srcUrl: Any? = null,
    @JsonProperty("status") val status: String? = null,
    @JsonProperty("tab") val tab: String? = null,
    @JsonProperty("topic") val topic: List<String> = emptyList(),
    @JsonProperty("unsafe") val unsafe: Int? = null
)

data class Developer(
    @JsonProperty("name") val name: String? = null,
    @JsonProperty("type") val type: String? = null,
    @JsonProperty("url") val url: String? = null
)

data class Maintainer(
    @JsonProperty("github") val github: String? = null
)

data class SrcOptions(
    @JsonProperty("directory") val directory: String? = null,
    @JsonProperty("is_fanon") val isFanon: Int? = null,
    @JsonProperty("is_mediawiki") val isMediawiki: Int? = null,
    @JsonProperty("is_wikipedia") val isWikipedia: Int? = null,
    @JsonProperty("language") val language: String? = null,
    @JsonProperty("min_abstract_length") val minAbstractLength: String? = null,
    @JsonProperty("skip_abstract") val skipAbstract: Int? = null,
    @JsonProperty("skip_abstract_paren") val skipAbstractParen: Int? = null,
    @JsonProperty("skip_end") val skipEnd: String? = null,
    @JsonProperty("skip_icon") val skipIcon: Int? = null,
    @JsonProperty("skip_image_name") val skipImageName: Int? = null,
    @JsonProperty("skip_qr") val skipQr: String? = null,
    @JsonProperty("source_skip") val sourceSkip: String? = null,
    @JsonProperty("src_info") val srcInfo: String? = null
)
