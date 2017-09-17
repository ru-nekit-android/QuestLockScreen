package ru.nekit.android.qls.quest.resourceLibrary;


import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class QuestResourceLibrary {

    private static final String TEXT_QUEST_FOLDER = "textQuestResources";
    private static final String TEXT_CAMOUFLAGE_DICTIONARY_FILE = "textCamouflageDictionary.txt";

    @NonNull
    private Context mContext;

    public QuestResourceLibrary(@NonNull Context context) {
        mContext = context;
    }

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

    public List<QuestVisualResourceItem> getVisualResourceItemsByGroup(QuestVisualResourceGroup group) {
        List<QuestVisualResourceItem> questVisualResourceItems = new ArrayList<>();
        for (QuestVisualResourceItem questVisualResourceItem : QuestVisualResourceItem.values()) {
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

    public QuestVisualResourceItem getVisualResourceItem(int itemId) {
        return QuestVisualResourceItem.getByItemId(itemId);
    }

    public QuestVisualResourceItem[] getVisualResourceItemList() {
        return QuestVisualResourceItem.values();
    }

}