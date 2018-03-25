package ru.nekit.android.qls.data.repository.store;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import ru.nekit.android.qls.data.utils.RuntimeTypeAdapterFactory;
import ru.nekit.android.qls.domain.model.quest.CurrentTimeQuest;
import ru.nekit.android.qls.domain.model.quest.FruitArithmeticQuest;
import ru.nekit.android.qls.domain.model.quest.NumberSummandQuest;
import ru.nekit.android.qls.domain.model.quest.Quest;
import ru.nekit.android.qls.domain.model.quest.TimeQuest;
import ru.nekit.android.qls.domain.model.quest.VisualRepresentationalNumberSummandQuest;

public class QuestStore extends AbstractJsonStateStore<Quest> {

    private final String CLASS_NAME_META = "@class";
    private final String VALUE = "value";
    private final String QUEST_AVAILABLE_VARIANTS = "quest.available_variants";

    private Gson json, jsonAvailableVariants;
    private JsonParser parser;
    private String internalQuestString;

    public QuestStore(SharedPreferences sharedPreferences) {
        super(sharedPreferences);
        GsonBuilder gsonBuilder = new GsonBuilder();
        jsonAvailableVariants = new GsonBuilder().create();
        final RuntimeTypeAdapterFactory<Quest> typeFactory = RuntimeTypeAdapterFactory
                .of(Quest.class, CLASS_NAME_META);
        for (Class<? extends Quest> classItem : getSupportsClasses()) {
            typeFactory.registerSubtype(classItem, classItem.getName());
        }
        gsonBuilder.registerTypeAdapterFactory(typeFactory);
        json = gsonBuilder.create();
        parser = new JsonParser();
    }

    @Override
    public String getName() {
        return "quest";
    }

    //TODO: IMPLEMENT!!!
    @SuppressWarnings("unchecked")
    private Class<? extends Quest>[] getSupportsClasses() {
        return new Class[]{
                NumberSummandQuest.class,
                FruitArithmeticQuest.class,
                TimeQuest.class,
                CurrentTimeQuest.class,
                VisualRepresentationalNumberSummandQuest.class,
                //PerimeterQuest.class,
                //MetricsQuest.class,
                //TextQuest.class,
        };
    }

    public void save(@NonNull Quest quest, String uuid) {
        JsonObject jsonObjectResult = (JsonObject) json.toJsonTree(quest, Quest.class);
        JsonArray jsonArrayAvailableVariants = new JsonArray();
        List<Object> availableVariants = quest.getAvailableAnswerVariants();
        if (availableVariants != null) {
            for (Object variantObject : availableVariants) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(CLASS_NAME_META, variantObject.getClass().getName());
                jsonObject.add(VALUE, jsonAvailableVariants.toJsonTree(variantObject));
                jsonArrayAvailableVariants.add(jsonObject);
            }
        }
        jsonObjectResult.add(QUEST_AVAILABLE_VARIANTS, jsonArrayAvailableVariants);
        internalQuestString = jsonObjectResult.toString();
        saveString(internalQuestString, uuid);
    }

    public String getQuestString() {
        return internalQuestString;
    }

    public Quest restore(@NonNull String uuid) {
        JsonObject jsonObjectQuest = parser.parse(restoreString(uuid)).getAsJsonObject();
        Quest quest = json.fromJson(jsonObjectQuest, Quest.class);
        JsonArray jsonArrayAvailableVariants =
                (JsonArray) jsonObjectQuest.get(QUEST_AVAILABLE_VARIANTS);
        List availableVariants = new ArrayList();
        for (JsonElement jsonElement : jsonArrayAvailableVariants) {
            JsonObject object = (JsonObject) jsonElement;
            Class availableVariantClass = null;
            try {
                availableVariantClass = Class.forName(object.get(CLASS_NAME_META).getAsString());
            } catch (ClassNotFoundException exp) {
                exp.printStackTrace();
            }
            if (availableVariantClass != null) {
                availableVariants.add(jsonAvailableVariants.fromJson(object.get(VALUE).toString(), availableVariantClass));
            }
        }
        quest.setAvailableAnswerVariants(availableVariants);
        return quest;
    }
}
