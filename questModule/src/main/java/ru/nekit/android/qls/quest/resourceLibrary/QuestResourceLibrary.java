package ru.nekit.android.qls.quest.resourceLibrary;


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

public class QuestResourceLibrary {

    private static final String TEXT_QUEST_FOLDER = "textQuestResources";
    private static final String TEXT_CAMOUFLAGE_DICTIONARY_FILE = "textCamouflageDictionary.txt";

    private static final Class[] LIBRARY = new Class[]{
            SimpleQuestVisualResourceItem.class,
            ChildrenToyQuestVisualResourceItem.class
    };

    @NonNull
    private final Context mContext;
    private final List<IQuestVisualResourceItem> mQuestVisualResourceItemList;

    @SuppressWarnings("unchecked")
    public QuestResourceLibrary(@NonNull Context context) {
        mContext = context;
        mQuestVisualResourceItemList = new ArrayList<>();
        for (Class libraryClass : LIBRARY) {
            IQuestVisualResourceItem[] questVisualResourceItems = null;
            if (libraryClass.isEnum()) {
                try {
                    Method method = libraryClass.getMethod("values");
                    try {
                        questVisualResourceItems = (IQuestVisualResourceItem[]) method.invoke(null);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            mQuestVisualResourceItemList.addAll(Arrays.asList(questVisualResourceItems));
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
    public List<IQuestVisualResourceItem> getVisualResourceItemsByGroup(QuestVisualResourceGroup group) {
        List<IQuestVisualResourceItem> questVisualResourceItems = new ArrayList<>();
        for (IQuestVisualResourceItem questVisualResourceItem : mQuestVisualResourceItemList) {
            QuestVisualResourceGroup[] groups = questVisualResourceItem.getGroups();
            if (groups != null) {
                for (QuestVisualResourceGroup groupItem : groups) {
                    if (groupItem.hasParent(group)) {
                        questVisualResourceItems.add(questVisualResourceItem);
                    }
                }
            }
        }
        return questVisualResourceItems;
    }

    public List<IQuestVisualResourceItem> getVisualResourceItemList() {
        return mQuestVisualResourceItemList;
    }

    public int getQuestVisualResourceItemId(@NonNull IQuestVisualResourceItem questVisualResourceItem) {
        return mQuestVisualResourceItemList.indexOf(questVisualResourceItem);
    }

    public IQuestVisualResourceItem getVisualResourceItem(int itemId) {
        return mQuestVisualResourceItemList.get(itemId);
    }
}