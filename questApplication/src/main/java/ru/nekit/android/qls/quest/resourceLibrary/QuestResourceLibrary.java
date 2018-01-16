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
            SimpleQuestVisualResource.class,
            ChildrenToysVisualResource.class
    };

    @NonNull
    private final Context mContext;
    private final List<IVisualResource> mQuestVisualResourceItemList;

    @SuppressWarnings("unchecked")
    public QuestResourceLibrary(@NonNull Context context) {
        mContext = context;
        mQuestVisualResourceItemList = new ArrayList<>();
        for (Class libraryClass : LIBRARY) {
            IVisualResource[] questVisualResourceItems = null;
            if (libraryClass.isEnum()) {
                try {
                    Method method = libraryClass.getMethod("values");
                    try {
                        questVisualResourceItems = (IVisualResource[]) method.invoke(null);
                    } catch (IllegalAccessException exp) {
                        exp.printStackTrace();
                    } catch (InvocationTargetException exp) {
                        exp.printStackTrace();
                    }
                } catch (NoSuchMethodException exp) {
                    exp.printStackTrace();
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
    public List<IVisualResource> getVisualResourceItemsByGroup(VisualResourceGroup group) {
        List<IVisualResource> questVisualResourceItems = new ArrayList<>();
        for (IVisualResource questVisualResourceItem : mQuestVisualResourceItemList) {
            VisualResourceGroup[] groups = questVisualResourceItem.getGroups();
            if (groups != null) {
                for (VisualResourceGroup groupItem : groups) {
                    if (groupItem.hasParent(group)) {
                        questVisualResourceItems.add(questVisualResourceItem);
                    }
                }
            }
        }
        return questVisualResourceItems;
    }

    public List<IVisualResource> getVisualResourceItems() {
        return mQuestVisualResourceItemList;
    }

    public int getQuestVisualResourceItemId(@NonNull IVisualResource questVisualResourceItem) {
        return mQuestVisualResourceItemList.indexOf(questVisualResourceItem);
    }

    public IVisualResource getVisualResourceItem(int itemId) {
        return mQuestVisualResourceItemList.get(itemId);
    }
}