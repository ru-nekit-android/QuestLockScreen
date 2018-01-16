package ru.nekit.android.qls.parentControl.setupWizard.steps;

import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.StatelessSection;
import ru.nekit.android.qls.EventBus;
import ru.nekit.android.qls.parentControl.ParentControlService;
import ru.nekit.android.qls.parentControl.R;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizard;
import ru.nekit.android.qls.parentControl.setupWizard.ParentControlSetupWizardFragment;
import ru.nekit.android.qls.pupil.Pupil;
import ru.nekit.android.qls.pupil.PupilManager;
import ru.nekit.android.qls.pupil.PupilSex;
import ru.nekit.android.qls.pupil.avatar.PupilAvatarViewBuilder;

import static ru.nekit.android.shared.R.dimen;

public class PupilListFragment extends ParentControlSetupWizardFragment
        implements EventBus.IEventHandler {

    private static final int BOUND_GROUP_POSITION = 0;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private PupilSection boundSection, waitingForBindingSection;
    private EventBus mEventBus;
    private RecyclerView recyclerView;

    public static PupilListFragment getInstance() {
        return new PupilListFragment();
    }

    @Override
    protected void onSetupStart(@NonNull View view) {
        setNextButtonText(R.string.label_bind_pupil);
        sectionAdapter = new SectionedRecyclerViewAdapter();
        PupilManager pupilManager = new PupilManager();
        List<Pupil> pupilList = pupilManager.getPupilList();
        for (int i = 0; i < 2; i++) {
            boolean isBound = i == BOUND_GROUP_POSITION;
            List<String> pupilUuidSectionList = getPupilUuidSectionListByCriteria(pupilList, isBound);
            PupilSection section = new PupilSection(pupilManager,
                    isBound ? R.string.title_selection_pupil_is_bound :
                            R.string.title_selection_pupil_is_waiting_for_binding,
                    pupilUuidSectionList);
            sectionAdapter.addSection(section);
            section.setVisible(pupilUuidSectionList.size() > 0);
            if (!isBound) {
                waitingForBindingSection = section;
            } else {
                boundSection = section;
            }
        }
        recyclerView = (RecyclerView) view.findViewById(R.id.list_pupil);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);
        mEventBus = new EventBus(getContext());
        mEventBus.handleEvents(this, ParentControlService.EVENT_BIND_PUPIL);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.sw_pupil_list;
    }

    private void showPupil(@NonNull String pupilUuid) {
        showSetupWizardStep(ParentControlSetupWizard.WizardStep.PUPIL_INFORMATION, pupilUuid);
    }

    @Override
    protected boolean nextAction() {
        showSetupWizardStep(ParentControlSetupWizard.WizardStep.BIND_PUPIL);
        return false;
    }

    @NonNull
    private List<String> getPupilUuidSectionListByCriteria(@NonNull List<Pupil> pupilList, boolean isBind) {
        List<String> pupilUuidSectionList = new ArrayList<>();
        for (Pupil pupil : pupilList) {
            if (pupil.isBind == isBind) {
                pupilUuidSectionList.add(pupil.getUuid());
            }
        }
        return pupilUuidSectionList;
    }

    @Override
    public void onDestroy() {
        recyclerView.setAdapter(null);
        recyclerView.setLayoutManager(null);
        mEventBus.stopHandleEvents(this);
        super.onDestroy();
    }

    @Override
    public void onEvent(@NonNull Intent intent) {
        String pupilUuid = intent.getStringExtra(ParentControlService.NAME_PUPIL_UUID);
        List<String> waitingForBindingPupilUuidList = waitingForBindingSection.getList();
        int index = 0;
        String boundPupilUuid = null;
        for (String pupilUuidItem : waitingForBindingPupilUuidList) {
            if (pupilUuidItem.equals(pupilUuid)) {
                boundPupilUuid = pupilUuidItem;
                break;
            }
            index++;
        }
        if (boundPupilUuid != null) {
            waitingForBindingSection.getList().remove(index);
            sectionAdapter.notifyItemRemovedFromSection(waitingForBindingSection, index);
            int size = boundSection.getList().size();
            boundSection.getList().add(boundPupilUuid);
            boundSection.setVisible(boundSection.getContentItemsTotal() > 0);
            waitingForBindingSection.setVisible(waitingForBindingSection.getContentItemsTotal() > 0);
            sectionAdapter.notifyItemInsertedInSection(boundSection, size);

        }
    }

    @NonNull
    @Override
    public String getEventBusName() {
        return getClass().getName();
    }

    private class PupilSection extends StatelessSection {

        private final Resources resources;
        private final PupilManager pupilManager;
        private final String title;
        private final List<String> list;

        PupilSection(@NonNull PupilManager pupilManager,
                     @StringRes int titleId,
                     @NonNull List<String> list) {
            super(new SectionParameters.Builder(R.layout.ill_pupil_item)
                    .headerResourceId(R.layout.ill_pupil_header)
                    .build());
            this.pupilManager = pupilManager;
            this.title = getString(titleId);
            this.list = list;
            resources = getContext().getResources();
        }

        @Override
        public int getContentItemsTotal() {
            return list.size();
        }

        List<String> getList() {
            return list;
        }

        @Override
        public RecyclerView.ViewHolder getItemViewHolder(View view) {
            return new PupilItemViewHolder(view);
        }

        @Override
        public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
            final PupilItemViewHolder itemHolder = (PupilItemViewHolder) holder;
            final Pupil pupil = pupilManager.getPupilByUuid(list.get(position));
            itemHolder.itemTitle.setText(pupil.name);
            itemHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPupil(pupil.getUuid());
                }
            });
            PupilAvatarViewBuilder.build(getContext(), pupil, itemHolder.pupilAvatarContainer);
            boolean hasAvatar = itemHolder.pupilAvatarContainer.getChildCount() > 0;
            itemHolder.pupilAvatarMaskContainer.setVisibility(
                    hasAvatar ? View.VISIBLE : View.GONE);
            if (hasAvatar) {
                float pupilAvatarScale = pupil.sex == PupilSex.BOY ?
                        (float) resources.getDimensionPixelSize(dimen.boy_avatar_height) /
                                resources.getDimensionPixelSize(dimen.boy_avatar_width)
                        :
                        (float) resources.getDimensionPixelSize(dimen.girl_avatar_height) /
                                resources.getDimensionPixelSize(dimen.girl_avatar_width);
                ViewGroup.LayoutParams pupilAvatarMaskContainerLayoutParams =
                        itemHolder.pupilAvatarMaskContainer.getLayoutParams();
                ViewGroup.LayoutParams pupilAvatarContainerLayoutParams =
                        itemHolder.pupilAvatarContainer.getLayoutParams();
                pupilAvatarContainerLayoutParams.height =
                        (int) (pupilAvatarMaskContainerLayoutParams.width * pupilAvatarScale);
                pupilAvatarContainerLayoutParams.width =
                        pupilAvatarMaskContainerLayoutParams.width;
                itemHolder.pupilAvatarContainer.requestLayout();
            }
        }

        @Override
        public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
            return new HeaderViewHolder(view);
        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.titleTextView.setText(title);
        }
    }

    private class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView titleTextView;

        HeaderViewHolder(View view) {
            super(view);
            titleTextView = (TextView) view.findViewById(R.id.tv_title);
        }
    }

    private class PupilItemViewHolder extends RecyclerView.ViewHolder {

        private final View view;
        private final ViewGroup pupilAvatarMaskContainer, pupilAvatarContainer;
        private final TextView itemTitle;

        PupilItemViewHolder(View view) {
            super(view);
            this.view = view;
            pupilAvatarContainer = (ViewGroup) view.findViewById(R.id.container_pupil_avatar);
            pupilAvatarMaskContainer = (ViewGroup) view.findViewById(R.id.container_pupil_avatar_mask);
            itemTitle = (TextView) view.findViewById(R.id.tv_item_title);
        }
    }
}