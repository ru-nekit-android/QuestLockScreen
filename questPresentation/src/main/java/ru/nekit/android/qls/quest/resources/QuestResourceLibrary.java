package ru.nekit.android.qls.quest.resources;


import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import ru.nekit.android.qls.quest.IStringHolder;
import ru.nekit.android.qls.quest.QuestContext;
import ru.nekit.android.qls.quest.resources.collections.ChildrenToysVisualResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.CoinVisualResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.LocalizedAdjectiveStringResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.LocalizedNounStringResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.SimpleVisualResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.VisualResourceGroupCollection;
import ru.nekit.android.qls.quest.resources.common.ILocalizedAdjectiveStringResourceHolder;
import ru.nekit.android.qls.quest.resources.common.ILocalizedNounStringResourceHolder;
import ru.nekit.android.qls.quest.resources.common.IVisualResourceHolder;
import ru.nekit.android.qls.utils.Declension;

public class QuestResourceLibrary {

    private static final String TEXT_QUEST_FOLDER = "textQuestResources";
    private static final String TEXT_CAMOUFLAGE_DICTIONARY_FILE = "textCamouflageDictionary.txt";

    private static final Class[] VISUAL_LIBRARY = new Class[]{
            SimpleVisualResourceCollection.class,
            ChildrenToysVisualResourceCollection.class,
            CoinVisualResourceCollection.class
    };

    @NonNull
    private final QuestContext mQuestContext;
    private final List<IVisualResourceHolder> mQuestVisualQuestResourceList;

    @SuppressWarnings("unchecked")
    public QuestResourceLibrary(@NonNull QuestContext questContext) {
        mQuestContext = questContext;
        mQuestVisualQuestResourceList = new ArrayList<>();
        for (Class libraryClass : VISUAL_LIBRARY) {
            IVisualResourceHolder[] questVisualResourceItems = null;
            if (libraryClass.isEnum()) {
                try {
                    Method method = libraryClass.getMethod("values");
                    try {
                        questVisualResourceItems = (IVisualResourceHolder[]) method.invoke(null);
                    } catch (IllegalAccessException exp) {
                        exp.printStackTrace();
                    } catch (InvocationTargetException exp) {
                        exp.printStackTrace();
                    }
                } catch (NoSuchMethodException exp) {
                    exp.printStackTrace();
                }
            }
            mQuestVisualQuestResourceList.addAll(Arrays.asList(questVisualResourceItems));
        }
    }

    @NonNull
    public List<String> getWordList(int wordLength) {
        AssetManager assetManager = mQuestContext.getAssets();
        String textCamouflageDictionaryPath = TEXT_QUEST_FOLDER +
                "/" +
                TEXT_CAMOUFLAGE_DICTIONARY_FILE;
        List<String> wordList = new ArrayList<>();
        try {
            InputStream textCamouflageStream = assetManager.open(textCamouflageDictionaryPath);
            if (textCamouflageStream != null) {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(textCamouflageStream));
                String word;
                while ((word = reader.readLine()) != null) {
                    if (word.length() == wordLength) {
                        wordList.add(word);
                    }
                }
                reader.close();
                textCamouflageStream.close();
            }
        } catch (IOException ignored) {
        }
        return wordList;
    }

    @NonNull
    public List<IVisualResourceHolder> getVisualResourceItemsByGroup(VisualResourceGroupCollection group) {
        List<IVisualResourceHolder> questVisualResourceItems = new ArrayList<>();
        for (IVisualResourceHolder questVisualResourceItem : mQuestVisualQuestResourceList) {
            VisualResourceGroupCollection[] groups = questVisualResourceItem.getGroups();
            if (groups != null) {
                for (VisualResourceGroupCollection groupItem : groups) {
                    if (groupItem.hasParent(group)) {
                        questVisualResourceItems.add(questVisualResourceItem);
                    }
                }
            }
        }
        return questVisualResourceItems;
    }

    public List<IVisualResourceHolder> getVisualQuestResourceList() {
        return mQuestVisualQuestResourceList;
    }

    public int getQuestVisualResourceId(@NonNull IVisualResourceHolder questVisualResourceItem) {
        return mQuestVisualQuestResourceList.indexOf(questVisualResourceItem);
    }

    @NonNull
    public IVisualResourceHolder getVisualQuestResource(int id) {
        return mQuestVisualQuestResourceList.get(id);
    }

    @NonNull
    private String declineAdjectiveByNoun(
            @NonNull LocalizedAdjectiveStringResourceCollection adjectiveBase,
            @NonNull LocalizedNounStringResourceCollection localizedNounStringResourceCollection,
            @NonNull String format
    ) {
        return localizedNounStringResourceCollection.hasOwnRule() ?
                String.format(format,
                        adjectiveBase,
                        localizedNounStringResourceCollection.getOwnRule()[0]
                )
                :
                declineAdjectiveByNoun(
                        adjectiveBase.getString(mQuestContext),
                        localizedNounStringResourceCollection.getString(mQuestContext),
                        format,
                        localizedNounStringResourceCollection.getGender(),
                        localizedNounStringResourceCollection.getIsPlural()
                );
    }

    @NonNull
    private String declineAdjectiveByNoun(
            @NonNull String adjectiveBase,
            @NonNull String nounBase,
            @NonNull String format,
            @NonNull Declension.Gender gender,
            boolean isPlural
    ) {
        return Declension.declineAdjectiveByNoun(adjectiveBase, nounBase, format,
                gender, isPlural);
    }

    public String localizeNounStringResourceIfNeed(
            @NonNull IStringHolder noun
    ) {
        if (Locale.getDefault().getLanguage().equals("ru")) {
            if (noun instanceof ILocalizedNounStringResourceHolder) {
                LocalizedNounStringResourceCollection localizedNounStringResourceCollection =
                        ((ILocalizedNounStringResourceHolder) noun).getLocalStringResource();
                if (localizedNounStringResourceCollection != null) {
                    return localizedNounStringResourceCollection.hasOwnRule() ?
                            String.format("%s%s",
                                    localizedNounStringResourceCollection.getString(mQuestContext),
                                    localizedNounStringResourceCollection.getOwnRule()[0]
                            )
                            :
                            Declension.declineNoun(localizedNounStringResourceCollection.getString(mQuestContext),
                                    localizedNounStringResourceCollection.getGender(),
                                    localizedNounStringResourceCollection.getIsPlural());
                }
            }
        }
        return noun.getString(mQuestContext);
    }

    @NonNull
    public String localizeAdjectiveAndNounStringResourceIfNeed(
            @NonNull IStringHolder adjective,
            @NonNull IStringHolder noun,
            @NonNull String formatString
    ) {
        if (Locale.getDefault().getLanguage().equals("ru")) {
            if (adjective instanceof ILocalizedAdjectiveStringResourceHolder &&
                    noun instanceof ILocalizedNounStringResourceHolder) {
                LocalizedAdjectiveStringResourceCollection localizedAdjectiveStringResourceCollection =
                        ((ILocalizedAdjectiveStringResourceHolder) adjective).getLocalStringResource();
                LocalizedNounStringResourceCollection localizedNounStringResourceCollection =
                        ((ILocalizedNounStringResourceHolder) noun).getLocalStringResource();
                if (localizedAdjectiveStringResourceCollection != null &&
                        localizedNounStringResourceCollection != null) {
                    return declineAdjectiveByNoun(
                            localizedAdjectiveStringResourceCollection,
                            localizedNounStringResourceCollection,
                            formatString);
                }
            }
        }
        return String.format(formatString, adjective.getString(mQuestContext), noun.getString(mQuestContext));
    }
}