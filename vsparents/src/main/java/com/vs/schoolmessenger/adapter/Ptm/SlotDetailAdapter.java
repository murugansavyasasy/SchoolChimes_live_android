package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SlotDetail;
import com.vs.schoolmessenger.util.Util_Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SlotDetailAdapter extends BaseAdapter {

    private final Context context;
    private final List<SlotDetail> slotDetails;
    private final SimpleDateFormat timeFormat;
    private final List<SlotDetail> allSlotDetails;
    private OnSlotSelectedListener onSlotSelectedListener;
    private final String isHyphen = "---";
    private final String isEqual = "==";
    public SlotDetailAdapter(Context context, List<SlotDetail> slotDetails, List<SlotDetail> allSlotDetails) {
        this.context = context;
        this.slotDetails = slotDetails;
        this.allSlotDetails = allSlotDetails;
        this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    }


    @Override
    public int getCount() {
        return slotDetails.size();
    }

    @Override
    public Object getItem(int position) {
        return slotDetails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.slots_timing, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        SlotDetail slotDetail = slotDetails.get(position);
        holder.tvFromTime.setText(slotDetail.getFrom_time() + " - " + slotDetail.getTo_time());
        convertView.setEnabled(true);
        TextView isTiming = holder.tvFromTime;
        RelativeLayout isTimeCard = holder.crdTiming;
        isTimeCard.setAlpha(1f);

        new Handler().postDelayed(() -> {
            processSlotSelection(slotDetail, isTiming, isTimeCard);
        }, 500);

        /** Is selected function */

        if (Util_Common.isHeaderSlotsIds.size() > 0) {
            for (int i = 0; i <= Util_Common.isHeaderSlotsIds.size() - 1; i++) {
                if (Util_Common.isHeaderSlotsIds.get(i).equals(slotDetail.getSlot_id() + isHyphen + slotDetail.getIsSpecificMeeting())) {
                    isTimeCard.setBackgroundResource(R.drawable.bg_slots_selected);
                    isTiming.setTextColor(context.getResources().getColor(R.color.clr_white));
                    isTimeCard.setAlpha(1f);
                    isTimeCard.setEnabled(true);
                } else {
                    if (isTimeCard.getBackground().getConstantState().equals(context.getResources().getDrawable(R.drawable.bg_slots_selected).getConstantState())) {
                        if (Util_Common.isSpecificSlot == slotDetail.getIsSpecificMeeting()) {
                            if (Util_Common.overlappingSlots.size() > 0) {
                                for (int s = 0; s <= Util_Common.overlappingSlots.size() - 1; s++) {
                                    if (Util_Common.overlappingSlots.get(s).equals(slotDetail.getSlot_id())) {
                                        isTimeCard.setEnabled(false);
                                        isTimeCard.setAlpha(0.5f);
                                    } else {
                                        isTimeCard.setBackgroundResource(R.drawable.bg_outline_slot_unselected);
                                        isTiming.setTextColor(context.getResources().getColor(R.color.clr_black));
                                        isTimeCard.setAlpha(1f);
                                        isTimeCard.setEnabled(true);
                                    }
                                }
                            } else {
                                isTimeCard.setBackgroundResource(R.drawable.bg_outline_slot_unselected);
                                isTiming.setTextColor(context.getResources().getColor(R.color.clr_black));
                                isTimeCard.setAlpha(1f);
                                isTimeCard.setEnabled(true);
                            }
                        } else {
                            isTimeCard.setEnabled(true);
                            isTimeCard.setBackgroundResource(R.drawable.bg_slots_selected);
                            isTiming.setTextColor(context.getResources().getColor(R.color.clr_white));
                        }
                    } else {
                        if (Util_Common.overlappingSlots.size() > 0) {
                            for (int s = 0; s <= Util_Common.overlappingSlots.size() - 1; s++) {
                                if (Util_Common.overlappingSlots.get(s).equals(slotDetail.getSlot_id())) {
                                    isTimeCard.setEnabled(false);
                                    isTimeCard.setAlpha(0.5f);
                                }
                            }
                        } else {
                            isTimeCard.setBackgroundResource(R.drawable.bg_outline_slot_unselected);
                            isTiming.setTextColor(context.getResources().getColor(R.color.clr_black));
                            isTimeCard.setAlpha(1f);
                            isTimeCard.setEnabled(true);
                        }
                    }
                }
            }
        }


        /** Slot Clicking */
        isTimeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util_Common.overlappingSlots.clear();
                Util_Common.isSpecificSlot = slotDetail.getIsSpecificMeeting();
                Util_Common.isSpecificSlotId = slotDetail.getSlot_id();
                if (Util_Common.isHeaderSlotsIds.size() > 0) {
                    for (int i = 0; i <= Util_Common.isHeaderSlotsIds.size() - 1; i++) {
                        if (Util_Common.isHeaderSlotsIds.get(i).contains(isHyphen + Util_Common.isSpecificSlot)) {
                            Util_Common.isHeaderSlotsIds.remove(i);
                            Util_Common.isSelectedTime.remove(i);
                        }
                    }
                }
                Util_Common.isSelectedTime.add(slotDetail.getFrom_time() + isEqual + slotDetail.getTo_time() + isEqual + slotDetail.getSlot_id() + isEqual + slotDetail.getIsBooking());
                Util_Common.isHeaderSlotsIds.add(Util_Common.isSpecificSlotId + isHyphen + Util_Common.isSpecificSlot);

                try {
                    isTimeOverLapping();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                if (onSlotSelectedListener != null) {
                    onSlotSelectedListener.onSlotSelected();
                }
            }
        });
        return convertView;
    }

    private void processSlotSelection(SlotDetail slotDetail, TextView isTiming, RelativeLayout isTimeCard) {
        if (slotDetail.getIsBooking() == 1) {
            Util_Common.isBookedIds.add(slotDetail.getIsSpecificMeeting());
        }
        if (Util_Common.isDataLoadingOver) {
            if (onSlotSelectedListener != null) {
                Util_Common.isDataLoadingOver = false;
                onSlotSelectedListener.onSlotSelected();
            }
        }

        if (Util_Common.isBookedIds.contains(slotDetail.getIsSpecificMeeting())) {
            if (slotDetail.getIsBooking() != 1) {
                isTimeCard.setEnabled(false);
                isTimeCard.setAlpha(0.5f);
            } else {
                Util_Common.isSelectedTime.add(slotDetail.getFrom_time() + isEqual + slotDetail.getTo_time() + isEqual + slotDetail.getSlot_id() + isEqual + slotDetail.getIsBooking());
                Util_Common.isHeaderSlotsIds.add(slotDetail.getSlot_id() + isHyphen + slotDetail.getIsSpecificMeeting());
                isTimeCard.setBackgroundResource(R.drawable.bg_slots_selected);
                isTiming.setTextColor(context.getResources().getColor(R.color.clr_white));
                isTimeCard.setAlpha(1f);
                isTimeCard.setEnabled(false);
                try {
                    isTimeOverLapping();
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }


    public interface OnSlotSelectedListener {
        void onSlotSelected();
    }

    public void setOnSlotSelectedListener(OnSlotSelectedListener listener) {
        this.onSlotSelectedListener = listener;
    }

    static class ViewHolder {
        TextView tvFromTime;
        RelativeLayout crdTiming;

        ViewHolder(View view) {
            tvFromTime = view.findViewById(R.id.lblTiming);
            crdTiming = view.findViewById(R.id.header);
        }
    }

    private void isTimeOverLapping() throws ParseException {

        Util_Common.isSelectedSlotIds.clear();
        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        for (String timeRangeStr : Util_Common.isSelectedTime) {
            String[] times = timeRangeStr.split(isEqual);
            String fromTime = times[0].trim();
            String toTime = times[1].trim();
            int slotId = Integer.parseInt(times[2].trim());
            int isBooking = Integer.parseInt(times[3].trim());
            if (isBooking != 1) {
                Util_Common.isSelectedSlotIds.add(slotId);
            }
            Date fromTimeDate = null;
            try {
                fromTimeDate = inputFormat.parse(fromTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            Date toTimeDate = null;
            try {
                toTimeDate = inputFormat.parse(toTime);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            for (SlotDetail otherSlot : allSlotDetails) {
                Date slotFrom = null;
                try {
                    slotFrom = timeFormat.parse(otherSlot.getFrom_time());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Date slotTo = null;
                try {
                    slotTo = timeFormat.parse(otherSlot.getTo_time());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                if (timeRangesOverlap(fromTimeDate, toTimeDate, slotFrom, slotTo, slotId)) {
                    Util_Common.overlappingSlots.add(otherSlot.getSlot_id());
                    for (int i = 0; i <= Util_Common.overlappingSlots.size() - 1; i++) {
                        if (Util_Common.overlappingSlots.get(i).equals(slotId)) {
                            Util_Common.overlappingSlots.remove(i);
                        }
                    }
                }
            }
        }
    }

    private boolean timeRangesOverlap(Date start1, Date end1, Date start2, Date end2, int slotId) {
        return start1.before(end2) && start2.before(end1);
    }
}