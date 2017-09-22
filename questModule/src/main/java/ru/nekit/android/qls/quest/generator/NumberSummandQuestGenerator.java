package ru.nekit.android.qls.quest.generator;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import ru.nekit.android.qls.quest.IQuest;
import ru.nekit.android.qls.quest.QuestionType;
import ru.nekit.android.qls.quest.math.MathematicalOperation;
import ru.nekit.android.qls.quest.math.MathematicalSignComparison;
import ru.nekit.android.qls.quest.types.quest.NumberSummandQuest;
import ru.nekit.android.qls.utils.Callable;
import ru.nekit.android.qls.utils.MathUtils;

public class NumberSummandQuestGenerator implements IQuestGenerator {

    private static final int CHANCE_EQUALITY_FOR_COMPARISON = 25;
    private static final int LEFT_NODE_INDEX = 0;
    private static final int RIGHT_NODE_INDEX = 1;

    private NumberSummandQuest mQuest;
    private int[] mMemberMaxValue, mMemberMinValue, mZeroChance, mAvailableValueArray;
    private int[][] mEachMemberMinMaxValue;
    private int mFlags;
    private Callable<Integer, Integer> mLeftNodeEachItemTransformFunction;

    public NumberSummandQuestGenerator(@Nullable QuestionType questionType) {
        mQuest = new NumberSummandQuest();
        mQuest.setQuestionType(questionType);
    }

    public void setMemberCounts(int leftNodeMemberCount, int rightNodeMemberCount) {
        mQuest.leftNode = leftNodeMemberCount == 0 ? null : new int[leftNodeMemberCount];
        mQuest.rightNode = rightNodeMemberCount == 0 ? null : new int[rightNodeMemberCount];
    }

    public void setMemberCounts(int memberCounts[]) {
        setMemberCounts(memberCounts[0], memberCounts[1]);
    }

    public void setLeftNodeMembersZeroValueChance(int[] zeroChance) {
        mZeroChance = zeroChance;
    }

    public void setFlags(int flags) {
        mFlags = flags;
    }

    public void setFlags(Flag... flags) {
        mFlags = 0;
        for (Flag flag : flags) {
            mFlags = mFlags | flag.value();
        }
    }

    private void setMembersMinAndMaxValues(int[] memberMinValue, int[] memberMaxValue) {
        mMemberMinValue = memberMinValue;
        mMemberMaxValue = memberMaxValue;
    }

    public void setMembersMinAndMaxValues(int[][] memberMinAndMaxValue) {
        setMembersMinAndMaxValues(memberMinAndMaxValue[0], memberMinAndMaxValue[1]);
    }

    public void setEachMemberMinAndMaxValues(int[][] eachMemberMinMaxValue) {
        mEachMemberMinMaxValue = eachMemberMinMaxValue;
    }

    public void setAvailableMemberValues(@NonNull int[] availableValues) {
        mAvailableValueArray = availableValues;
    }

    public <T> void setAvailableMemberValues(@NonNull T[] availableValues, Callable<T, Integer> mapper) {
        final int length = availableValues.length;
        mAvailableValueArray = new int[length];
        for (int i = 0; i < length; i++) {
            mAvailableValueArray[i] = mapper.call(availableValues[i]);
        }
    }

    public <T> void setAvailableMemberValues(@NonNull List<T> availableValues, Callable<T, Integer> mapper) {
        final int length = availableValues.size();
        mAvailableValueArray = new int[length];
        for (int i = 0; i < length; i++) {
            mAvailableValueArray[i] = mapper.call(availableValues.get(i));
        }
    }

    public void setAvailableMemberValues(@NonNull Enum[] availableValues) {
        setAvailableMemberValues(availableValues, new Callable<Enum, Integer>() {
            @Override
            public Integer call(Enum value) {
                return value.ordinal();
            }
        });
    }

    private void generateArrayRandomAvailableValues(NumberSummandQuest quest) {
        for (int i = 0; i < quest.leftNode.length; i++) {
            quest.leftNode[i] = mAvailableValueArray[MathUtils.randUnsignedInt(mAvailableValueArray.length - 1)];
        }
    }

    private int getMemberMinValue(int nodeIndex, int position) {
        int memberMinValue = 0;
        if (mMemberMinValue != null) {
            memberMinValue = mMemberMinValue[nodeIndex];
        } else if (mEachMemberMinMaxValue != null) {
            memberMinValue = mEachMemberMinMaxValue[position][0];
        }
        return memberMinValue;
    }

    private int getMemberMaxValue(int nodeIndex, int position) {
        int memberMaxValue = 0;
        if (mMemberMaxValue != null) {
            memberMaxValue = mMemberMaxValue[nodeIndex];
        } else if (mEachMemberMinMaxValue != null) {
            memberMaxValue = mEachMemberMinMaxValue[position][1];
        }
        return memberMaxValue;
    }

    private void generateArrayRandomValuesInRange(NumberSummandQuest quest, int nodeIndex) {
        int[] memberArray = (nodeIndex == LEFT_NODE_INDEX ? mQuest.leftNode : mQuest.rightNode);
        if (memberArray != null) {
            int memberCount = memberArray.length;
            int[] memberRandomValues;
            if (nodeIndex == LEFT_NODE_INDEX) {
                quest.leftNode = new int[memberCount];
                memberRandomValues = quest.leftNode;
            } else {
                quest.rightNode = new int[memberCount];
                memberRandomValues = quest.rightNode;
            }
            int sumValue;
            for (int i = 0; i < memberCount; i++) {
                int randomValue = MathUtils.randInt(getMemberMinValue(nodeIndex, i),
                        getMemberMaxValue(nodeIndex, i));
                if ((mFlags & Flag.ONLY_POSITIVE_SUMMANDS.value()) != 0) {
                    memberRandomValues[i] = Math.abs(randomValue);
                } else {
                    memberRandomValues[i] = (MathUtils.randBoolean() ? -1 : 1) *
                            randomValue;
                }
                if (mZeroChance != null) {
                    memberRandomValues[i] = (MathUtils.randUnsignedInt(100) <
                            mZeroChance[i] ? 0 : 1) * memberRandomValues[i];
                }
                sumValue = MathUtils.sum(memberRandomValues);
                if (((mFlags &
                        Flag.AVOID_NEGATIVE_ANSWER_WHILE_CALCULATION.value()) != 0
                        && sumValue < 0)
                        || (mFlags & Flag.AVOID_ZERO_ANSWER_WHILE_CALCULATION.value()) != 0
                        && sumValue == 0) {
                    memberRandomValues[i] = -memberRandomValues[i];
                }
            }
            sumValue = MathUtils.sum(memberRandomValues);
            if (((mFlags & Flag.AVOID_NEGATIVE_ANSWER.value()) != 0 &&
                    sumValue < 0) || ((mFlags & Flag.AVOID_ZERO_ANSWER.value()) != 0 &&
                    sumValue == 0)) {
                for (int i = 0; i < memberCount; i++) {
                    memberRandomValues[i] = -memberRandomValues[i];
                }
                for (int i = 0; i < memberCount; i++) {
                    if (memberRandomValues[i] > 0) {
                        if (getMemberMaxValue(nodeIndex, i) > memberRandomValues[i]) {
                            memberRandomValues[i]++;
                        }
                        break;
                    }
                }
            }
            if ((mFlags & Flag.POSITIVE_FIRST_SUMMAND.value()) != 0) {
                memberRandomValues[0] = Math.abs(memberRandomValues[0]);
            }
        }
    }

    private void generateRightNodeEqualsLeftNode(NumberSummandQuest quest) {
        if (mQuest.rightNode != null) {
            generateArrayRandomValuesInRange(quest, RIGHT_NODE_INDEX);
            int rightMembersCount = mQuest.rightNode.length;
            int differenceValue = quest.getLeftNodeSum() - quest.getRightNodeSum();
            int residueValue = Math.abs(differenceValue);
            if (differenceValue != 0) {
                for (int i = 0; i < rightMembersCount; i++) {
                    if (residueValue != 0) {
                        int randomValue = Math.max(1, Math.min(residueValue, MathUtils.randInt(0,
                                differenceValue > 0 ? getMemberMaxValue(RIGHT_NODE_INDEX, i)
                                        - Math.abs(quest.rightNode[i]) : Math.abs(quest.rightNode[i]))));
                        if (differenceValue > 0) {
                            quest.rightNode[i] += randomValue;
                        } else {
                            quest.rightNode[i] -= randomValue;
                            if (quest.rightNode[i] == 0) {
                                quest.rightNode[i]++;
                                randomValue--;
                            }
                        }
                        residueValue -= randomValue;
                    } else {
                        return;
                    }
                }
                if (residueValue != 0) {
                    if (differenceValue > 0) {
                        quest.rightNode[rightMembersCount - 1] += residueValue;
                    } else {
                        quest.rightNode[rightMembersCount - 1] -= residueValue;
                    }
                }
                if ((mFlags & Flag.ONLY_POSITIVE_SUMMANDS.value()) != 0) {
                    for (int i = 0; i < rightMembersCount; i++) {
                        quest.rightNode[i] = Math.abs(quest.rightNode[i]);
                    }
                }
            }
        }
    }

    @Override
    public IQuest generate() {
        if (mAvailableValueArray == null) {
            generateArrayRandomValuesInRange(mQuest, LEFT_NODE_INDEX);
            switch (mQuest.getQuestionType()) {

                case COMPARISON:

                    if (MathUtils.randPositiveInt(100) >= CHANCE_EQUALITY_FOR_COMPARISON) {
                        generateArrayRandomValuesInRange(mQuest, RIGHT_NODE_INDEX);
                    } else {
                        generateRightNodeEqualsLeftNode(mQuest);
                    }
                    mQuest.setAvailableAnswerVariants(MathematicalSignComparison.values());

                    break;

                case UNKNOWN_MEMBER:

                    generateRightNodeEqualsLeftNode(mQuest);
                    mQuest.unknownMemberIndex =
                            MathUtils.randUnsignedInt(mQuest.leftNode.length - 1);

                    break;

                case UNKNOWN_OPERATION:

                    generateRightNodeEqualsLeftNode(mQuest);
                    mQuest.unknownOperatorIndex =
                            MathUtils.randUnsignedInt(mQuest.leftNode.length - 2);
                    mQuest.setAvailableAnswerVariants(new Object[]{MathematicalOperation.ADDITION,
                            MathematicalOperation.SUBTRACTION});

                    break;
            }
        } else {
            Object[] availableValueArray = new Object[mAvailableValueArray.length];
            int index = 0;
            for (int value : mAvailableValueArray) {
                availableValueArray[index++] = value;
            }
            mQuest.setAvailableAnswerVariants(availableValueArray);
            generateArrayRandomAvailableValues(mQuest);
            switch (mQuest.getQuestionType()) {

                case UNKNOWN_MEMBER:

                    mQuest.unknownMemberIndex = MathUtils.randUnsignedInt(mQuest.leftNode.length - 1);

                    break;

                case UNKNOWN_OPERATION:
                case COMPARISON:

                    break;

            }
        }
        if (mLeftNodeEachItemTransformFunction != null && mQuest.leftNode != null) {
            for (int i = 0; i < mQuest.leftNode.length; i++) {
                mQuest.leftNode[i] = mLeftNodeEachItemTransformFunction.call(mQuest.leftNode[i]);
            }
        }
        return mQuest;
    }

    public void setLeftNodeEachItemTransformationFunction(Callable<Integer, Integer> value) {
        mLeftNodeEachItemTransformFunction = value;
    }

    public enum Flag {

        POSITIVE_FIRST_SUMMAND,
        AVOID_NEGATIVE_ANSWER,
        AVOID_NEGATIVE_ANSWER_WHILE_CALCULATION,
        AVOID_ZERO_ANSWER,
        AVOID_ZERO_ANSWER_WHILE_CALCULATION,
        ONLY_POSITIVE_SUMMANDS;

        public int value() {
            return (int) Math.pow(2, ordinal());
        }

    }
}