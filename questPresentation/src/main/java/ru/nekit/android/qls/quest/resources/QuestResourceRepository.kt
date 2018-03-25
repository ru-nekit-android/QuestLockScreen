package ru.nekit.android.qls.quest.resources

import android.content.Context
import ru.nekit.android.qls.data.providers.IStringListResourceProvider
import ru.nekit.android.qls.data.providers.IStringResourceProvider
import ru.nekit.android.qls.domain.model.resources.*
import ru.nekit.android.qls.domain.model.resources.common.IResourceHolder
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceHolder
import ru.nekit.android.qls.domain.model.resources.common.IVisualResourceWithGroupHolder
import ru.nekit.android.qls.domain.repository.IQuestResourceRepository
import ru.nekit.android.qls.domain.utils.Declension
import ru.nekit.android.qls.quest.resources.common.ILocalizedAdjectiveStringResourceProvider
import ru.nekit.android.qls.quest.resources.common.ILocalizedNounStringResourceHolder
import ru.nekit.android.qls.quest.resources.representation.getRepresentation
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

class QuestResourceRepository(private val context: Context) : IQuestResourceRepository {

    private val questVisualQuestResourceList: MutableList<IVisualResourceWithGroupHolder>

    override val visualResourceList: List<IVisualResourceHolder>
        get() = questVisualQuestResourceList

    init {
        questVisualQuestResourceList = ArrayList()
        for (libraryClass in VISUAL_LIBRARY) {
            try {
                val method = libraryClass.getMethod("values")
                questVisualQuestResourceList.addAll((method.invoke(null)
                        as Array<IVisualResourceWithGroupHolder>).toList())
            } catch (exp: Throwable) {
            }
        }
    }

    override fun getWordList(wordLength: Int): List<String> {
        val assetManager = context.assets
        val textCamouflageDictionaryPath = TEXT_QUEST_FOLDER +
                "/" +
                TEXT_CAMOUFLAGE_DICTIONARY_FILE
        val wordList = ArrayList<String>()
        try {
            val textCamouflageStream = assetManager.open(textCamouflageDictionaryPath)
            if (textCamouflageStream != null) {
                val reader = BufferedReader(
                        InputStreamReader(textCamouflageStream))
                var word: String?
                do {
                    word = reader.readLine()
                    if (word.length == wordLength) {
                        wordList.add(word)
                    }
                } while (word != null)
                reader.close()
                textCamouflageStream.close()
            }
        } catch (ignored: IOException) {
        }
        return wordList
    }

    override fun getVisualResourceItemsByGroup(group: ResourceGroupCollection): List<IVisualResourceHolder> {
        val result = ArrayList<IVisualResourceHolder>()
        questVisualQuestResourceList.forEach { item ->
            item.groups.asSequence()
                    .filter { it.hasParent(group) }
                    .forEach { result += item }
        }
        return result
    }

    override fun getVisualResourceItemIdsByGroup(group: ResourceGroupCollection): List<Int> =
            getVisualResourceItemsByGroup(group).map {
                getVisualResourceItemId(it)
            }

    override fun getVisualResourceItemId(item: IVisualResourceHolder): Int {
        return questVisualQuestResourceList.indexOf(item)
    }

    override fun getVisualResourceItemById(id: Int): IVisualResourceHolder {
        return questVisualQuestResourceList[id]
    }

    override fun getNounStringRepresentation(resourceHolder: IVisualResourceHolder): String {
        var result: String? = null
        val representation = resourceHolder.getRepresentation()
        if (representation is ILocalizedNounStringResourceHolder) {
            if (Locale.getDefault().language == "ru") {
                representation.localizedStringResource?.apply {
                    getRepresentation().apply {
                        val string = getString(context)
                        result = if (hasOwnRule())
                            "$string${ownRule[0]}"
                        else
                            Declension.declineNoun(string, gender, isPlural)
                    }
                }
            }
        } else {
            result = getStringFromRepresentation(representation)
        }
        return result ?: ""
    }

    private fun getStringFromRepresentation(representation: Any): String? {
        var result: String? = null
        if (representation is IStringListResourceProvider)
            result = representation.getRandomString(context)
        if (representation is IStringResourceProvider)
            result = representation.getString(context)
        return result
    }

    override fun localizeAdjectiveAndNounStringResourceIfNeed(adjectiveItemHolder: IResourceHolder,
                                                              nounItemHolder: IVisualResourceHolder,
                                                              formatString: String): String {
        var result: String? = null
        val adjectiveRepresentation = adjectiveItemHolder.getRepresentation()
        val nounRepresentation = nounItemHolder.getRepresentation()
        if (Locale.getDefault().language == "ru") {
            if (adjectiveRepresentation is ILocalizedAdjectiveStringResourceProvider &&
                    nounRepresentation is ILocalizedNounStringResourceHolder) {
                val localizedAdjectiveStringResource = adjectiveRepresentation.localizedStringResource
                val localizedNounStringResource = nounRepresentation.localizedStringResource
                if (localizedAdjectiveStringResource != null &&
                        localizedNounStringResource != null) {
                    result = declineAdjectiveByNoun(
                            localizedAdjectiveStringResource,
                            localizedNounStringResource,
                            formatString)
                }
            }
        } else {
            val adjectiveString: String? = getStringFromRepresentation(adjectiveRepresentation)
            val nounString: String? = getStringFromRepresentation(nounRepresentation)
            result = String.format(formatString,
                    adjectiveString,
                    nounString)

        }
        return result ?: ""
    }

    private fun declineAdjectiveByNoun(
            adjective: LocalizedAdjectiveStringResourceCollection,
            noun: LocalizedNounStringResourceCollection,
            format: String
    ): String {
        val adjectiveRepresentation = adjective.getRepresentation()
        val nounRepresentation = noun.getRepresentation()
        return if (nounRepresentation.hasOwnRule())
            String.format(format,
                    adjectiveRepresentation.getString(context),
                    nounRepresentation.ownRule[0]
            )
        else
            Declension.declineAdjectiveByNoun(
                    adjectiveRepresentation.getString(context),
                    nounRepresentation.getString(context),
                    format,
                    nounRepresentation.gender,
                    nounRepresentation.isPlural
            )
    }

    companion object {

        private const val TEXT_QUEST_FOLDER = "textQuestResources"
        private const val TEXT_CAMOUFLAGE_DICTIONARY_FILE = "textCamouflageDictionary.txt"

        private val VISUAL_LIBRARY = listOf(SimpleVisualResourceCollection::class.java,
                ChildrenToysVisualResourceCollection::class.java,
                CoinVisualResourceCollection::class.java)
    }
}