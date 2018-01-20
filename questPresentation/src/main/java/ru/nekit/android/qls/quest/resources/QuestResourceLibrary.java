package ru.nekit.android.qls.quest.resources;


import android.content.Context;
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

import ru.nekit.android.qls.quest.resources.collections.ChildrenToysVisualQuestResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.LocalizedStringResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.SimpleQuestVisualQuestResourceCollection;
import ru.nekit.android.qls.quest.resources.collections.VisualQuestResourceGroupCollection;
import ru.nekit.android.qls.quest.resources.common.IVisualQuestResourceHolder;
import ru.nekit.android.qls.utils.Declension;

public class QuestResourceLibrary {

    private static final String TEXT_QUEST_FOLDER = "textQuestResources";
    private static final String TEXT_CAMOUFLAGE_DICTIONARY_FILE = "textCamouflageDictionary.txt";

    private static final Class[] LIBRARY = new Class[]{
            SimpleQuestVisualQuestResourceCollection.class,
            ChildrenToysVisualQuestResourceCollection.class
    };

    @NonNull
    private final Context mContext;
    private final List<IVisualQuestResourceHolder> mQuestVisualQuestResourceList;

    @SuppressWarnings("unchecked")
    public QuestResourceLibrary(@NonNull Context context) {
        mContext = context;
        mQuestVisualQuestResourceList = new ArrayList<>();
        for (Class libraryClass : LIBRARY) {
            IVisualQuestResourceHolder[] questVisualResourceItems = null;
            if (libraryClass.isEnum()) {
                try {
                    Method method = libraryClass.getMethod("values");
                    try {
                        questVisualResourceItems = (IVisualQuestResourceHolder[]) method.invoke(null);
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
        AssetManager assetManager = mContext.getAssets();
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
    public List<IVisualQuestResourceHolder> getVisualResourceItemsByGroup(VisualQuestResourceGroupCollection group) {
        List<IVisualQuestResourceHolder> questVisualResourceItems = new ArrayList<>();
        for (IVisualQuestResourceHolder questVisualResourceItem : mQuestVisualQuestResourceList) {
            VisualQuestResourceGroupCollection[] groups = questVisualResourceItem.getGroups();
            if (groups != null) {
                for (VisualQuestResourceGroupCollection groupItem : groups) {
                    if (groupItem.hasParent(group)) {
                        questVisualResourceItems.add(questVisualResourceItem);
                    }
                }
            }
        }
        return questVisualResourceItems;
    }

    public List<IVisualQuestResourceHolder> getVisualQuestResourceList() {
        return mQuestVisualQuestResourceList;
    }

    public int getQuestVisualResourceId(@NonNull IVisualQuestResourceHolder questVisualResourceItem) {
        return mQuestVisualQuestResourceList.indexOf(questVisualResourceItem);
    }

    @NonNull
    public IVisualQuestResourceHolder getVisualQuestResource(int id) {
        return mQuestVisualQuestResourceList.get(id);
    }

    public String declineAdjectiveByNoun(
            @NonNull Context context,
            @NonNull String adjectiveBase,
            @NonNull String format,
            @NonNull LocalizedStringResourceCollection localizedStringResourceCollection) {
        return declineAdjectiveByNoun(adjectiveBase,
                localizedStringResourceCollection.getName(context),
                format,
                localizedStringResourceCollection.getGender(),
                localizedStringResourceCollection.getIsPlural()
        );
    }

    private String declineAdjectiveByNoun(
            @NonNull String adjectiveBase,
            @NonNull String nounBase,
            @NonNull String format,
            @NonNull Declension.Gender gender,
            boolean isPlural) {
        if (Locale.getDefault().getLanguage().equals("ru")) {
            return Declension.declineAdjectiveByNoun(adjectiveBase, nounBase, format,
                    gender, isPlural);
        }
        return String.format(format, adjectiveBase, nounBase);
    }
}