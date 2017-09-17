package ru.nekit.android.qls.quest.persistance;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

import ru.nekit.android.qls.PreferencesUtil;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.types.CurrentTimeQuest;
import ru.nekit.android.qls.quest.types.FruitArithmeticQuest;
import ru.nekit.android.qls.quest.types.IQuestVisualRepresentation;
import ru.nekit.android.qls.quest.types.MetricsQuest;
import ru.nekit.android.qls.quest.types.NumberSummandQuest;
import ru.nekit.android.qls.quest.types.PerimeterQuest;
import ru.nekit.android.qls.quest.types.QuestVisualRepresentationList;
import ru.nekit.android.qls.quest.types.TextQuest;
import ru.nekit.android.qls.quest.types.TimeQuest;
import ru.nekit.android.qls.utils.AbstractStateSaver;
import ru.nekit.android.qls.utils.RuntimeTypeAdapterFactory;

public class QuestSaver extends AbstractStateSaver<IQuest> {

    private final String CLASS_NAME_META = "@class";
    private final String VALUE = "value";
    private final String QUEST_AVAILABLE_VARIANTS = "quest.available_variants";
    private final String QUEST_VISUAL_REPRESENTATION = "quest.visual_representation";

    private Gson mGson, mGsonAvailableVariants;
    private JsonParser mParser;

    public QuestSaver(@NonNull Context context) {
        super(context);
        GsonBuilder gsonBuilder = new GsonBuilder();
        mGsonAvailableVariants = new GsonBuilder().create();
        final RuntimeTypeAdapterFactory<IQuest> typeFactory = RuntimeTypeAdapterFactory
                .of(IQuest.class, CLASS_NAME_META);
        for (Class<? extends IQuest> classItem : getSupportsClasses()) {
            typeFactory.registerSubtype(classItem, classItem.getName());
        }
        gsonBuilder.registerTypeAdapterFactory(typeFactory);
        mGson = gsonBuilder.create();
        mParser = new JsonParser();
    }

    @Override
    public String getName() {
        return "quest";
    }

    @Override
    protected String getUUID() {
        return PreferencesUtil.getString(Pupil.NAME_CURRENT);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends IQuest>[] getSupportsClasses() {
        return new Class[]{
                PerimeterQuest.class,
                MetricsQuest.class,
                NumberSummandQuest.class,
                FruitArithmeticQuest.class,
                TimeQuest.class,
                TextQuest.class,
                CurrentTimeQuest.class
        };
    }

    public void save(@NonNull IQuest quest) {
        JsonObject jsonObjectResult = (JsonObject) mGson.toJsonTree(quest);
        JsonArray jsonArrayAvailableVariants = new JsonArray();
        Object[] availableVariants = quest.getAvailableAnswerVariants();
        if (availableVariants != null) {
            for (Object variantObject : availableVariants) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(CLASS_NAME_META, variantObject.getClass().getName());
                jsonObject.add(VALUE, mGsonAvailableVariants.toJsonTree(variantObject));
                jsonArrayAvailableVariants.add(jsonObject);
            }
        }
        jsonObjectResult.add(QUEST_AVAILABLE_VARIANTS, jsonArrayAvailableVariants);
        if (quest instanceof IQuestVisualRepresentation) {
            JsonArray jsonArrayQuestVisualResourceItems = new JsonArray();
            List<Integer> questVisualRepresentationList = ((IQuestVisualRepresentation) quest).getVisualRepresentationList().getIdsList();
            for (int questVisualResourceItemId : questVisualRepresentationList) {
                jsonArrayQuestVisualResourceItems.add(questVisualResourceItemId);
            }
            jsonObjectResult.add(QUEST_VISUAL_REPRESENTATION, jsonArrayQuestVisualResourceItems);
        }
        saveString(jsonObjectResult.toString());
    }

    public IQuest restore() {
        JsonObject jsonObjectQuest = mParser.parse(restoreString()).getAsJsonObject();
        IQuest quest = mGson.fromJson(jsonObjectQuest, IQuest.class);
        JsonArray jsonArrayAvailableVariants =
                (JsonArray) jsonObjectQuest.get(QUEST_AVAILABLE_VARIANTS);
        Object[] availableVariants = new Object[jsonArrayAvailableVariants.size()];
        int index = 0;
        for (JsonElement jsonElement : jsonArrayAvailableVariants) {
            JsonObject object = (JsonObject) jsonElement;
            Class availableVariantClass = null;
            try {
                availableVariantClass = Class.forName(object.get(CLASS_NAME_META).getAsString());
            } catch (ClassNotFoundException exp) {
                exp.printStackTrace();
            }
            if (availableVariantClass != null) {
                availableVariants[index++] = mGsonAvailableVariants.fromJson(object.get(VALUE).toString(), availableVariantClass);
            }
        }
        quest.setAvailableAnswerVariants(availableVariants);
        if (jsonObjectQuest.has(QUEST_VISUAL_REPRESENTATION)) {
            JsonArray jsonArrayVisualRepresentationItems =
                    (JsonArray) jsonObjectQuest.get(QUEST_VISUAL_REPRESENTATION);
            QuestVisualRepresentationList questVisualRepresentationList =
                    new QuestVisualRepresentationList();
            for (JsonElement jsonElement : jsonArrayVisualRepresentationItems) {
                questVisualRepresentationList.add(jsonElement.getAsInt());
            }
            ((IQuestVisualRepresentation) quest).setVisualRepresentationList(questVisualRepresentationList);
        }
        return quest;
    }
}
