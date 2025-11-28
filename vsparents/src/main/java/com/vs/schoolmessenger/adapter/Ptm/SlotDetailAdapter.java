package com.vs.schoolmessenger.adapter.Ptm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vs.schoolmessenger.R;
import com.vs.schoolmessenger.model.SlotDetail;
import com.vs.schoolmessenger.util.Util_Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SlotDetailAdapter extends BaseAdapter {

    private final Context context;
    private final List<SlotDetail> slotDetails;     // slots for this child list
    private final List<SlotDetail> allSlotDetails;  // all slots across parents (required for global overlap)
    private OnSlotSelectedListener onSlotSelectedListener;
    private final SimpleDateFormat timeFormat;
    private final String isHyphen = "---";
    private final String isEqual = "==";

    // Whether this child group contains a "my booking" slot.
    // If true, whole child list shows disabled except the my-booking slot.
    private boolean hasMyBookingInThisList = false;

    public SlotDetailAdapter(Context context, List<SlotDetail> slotDetails, List<SlotDetail> allSlotDetails) {
        this.context = context;
        this.slotDetails = slotDetails;
        this.allSlotDetails = allSlotDetails;
        this.timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

        // Initialize whether this list contains my booking
        for (SlotDetail s : slotDetails) {
            if (s.getIsMyBooking() == 1) {
                hasMyBookingInThisList = true;
                break;
            }
        }
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
        RelativeLayout card = holder.crdTiming;
        TextView text = holder.tvFromTime;

        // Set text
        text.setText(slotDetail.getFrom_time() + " - " + slotDetail.getTo_time());

        // Reset visual defaults
        card.setEnabled(true);
        card.setAlpha(1f);
        card.setBackgroundResource(R.drawable.bg_outline_slot_unselected);
        text.setTextColor(context.getResources().getColor(R.color.clr_black));

        // Priority visual logic:
        // 1) My booking (this slot) -> orange
        if (slotDetail.getIsMyBooking() == 1) {
            card.setBackgroundResource(R.drawable.bg_slots_selected);
            text.setTextColor(context.getResources().getColor(R.color.clr_white));
            card.setEnabled(true);
        }
        // 2) If this child list has a my-booking slot, disable (gray) other slots in this list
        else if (hasMyBookingInThisList) {
            card.setBackgroundResource(R.drawable.bg_gray_button);
            text.setTextColor(context.getResources().getColor(R.color.clr_white));
            card.setEnabled(false);
        }
        // 3) Booked by others -> green
        else if (slotDetail.getIsBooking() == 1 && slotDetail.getIsMyBooking() == 0) {
            card.setBackgroundResource(R.drawable.bg_btn_approve);
            text.setTextColor(context.getResources().getColor(R.color.clr_white));
            card.setEnabled(false);
        }
        // 4) Overlapping global disabling (computed in isTimeOverLapping())
        else if (Util_Common.overlappingSlots.contains(slotDetail.getSlot_id())) {
            card.setBackgroundResource(R.drawable.bg_gray_button);
            text.setTextColor(context.getResources().getColor(R.color.clr_white));
            card.setEnabled(false);
        }
        // 5) Selected slot (highlight)
        else if (Util_Common.isHeaderSlotsIds.contains(slotDetail.getSlot_id() + isHyphen + slotDetail.getIsSpecificMeeting())) {
            card.setBackgroundResource(R.drawable.bg_slots_selected);
            text.setTextColor(context.getResources().getColor(R.color.clr_white));
        }

        // Click logic: only allow if slot isn't booked/myBooking and this child list isn't locked by myBooking
        card.setOnClickListener(v -> {
            if (slotDetail.getIsMyBooking() == 1 || slotDetail.getIsBooking() == 1 || hasMyBookingInThisList) return;

            // Remove previous selection for same specific meeting group
            for (int i = 0; i < Util_Common.isHeaderSlotsIds.size(); i++) {
                if (Util_Common.isHeaderSlotsIds.get(i).contains(isHyphen + slotDetail.getIsSpecificMeeting())) {
                    Util_Common.isHeaderSlotsIds.remove(i);
                    Util_Common.isSelectedTime.remove(i);
                    i--;
                }
            }

            // Add clicked slot to selected list
            Util_Common.isSelectedTime.add(slotDetail.getFrom_time() + isEqual + slotDetail.getTo_time()
                    + isEqual + slotDetail.getSlot_id() + isEqual + slotDetail.getIsBooking());
            Util_Common.isHeaderSlotsIds.add(slotDetail.getSlot_id() + isHyphen + slotDetail.getIsSpecificMeeting());

            // Recalculate overlaps globally
            try {
                isTimeOverLapping();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Notify listener
            if (onSlotSelectedListener != null) {
                boolean isAnySelected = !Util_Common.isSelectedTime.isEmpty();
                onSlotSelectedListener.onSlotSelected(isAnySelected);
            }

            // Refresh visuals
            notifyDataSetChanged();
        });

        try {
            isTimeOverLapping();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    /**
     * Calculates overlaps globally:
     * - Include slots from Util_Common.isSelectedTime and any slot with isMyBooking==1
     * - Mark overlapping slots' isBooking=2 (disabled) unless they are booked or myBooking
     * - Re-enable slots that are no longer overlapping (set isBooking=0) if not booked/myBooking
     */
    private void isTimeOverLapping() throws ParseException {
        Util_Common.overlappingSlots.clear();
        Util_Common.isSelectedSlotIds.clear();

        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm a", Locale.US);

        // Use a set for IDs for faster unique contains checks
        Set<Integer> selectedIds = new HashSet<>();

        // 1) Include all slots with isMyBooking == 1 as "selected" for overlap checks
        for (SlotDetail s : allSlotDetails) {
            if (s.getIsMyBooking() == 1) {
                selectedIds.add(s.getSlot_id());
            }
        }

        // 2) Include any user-selected slots from Util_Common.isSelectedTime
        for (String timeRangeStr : Util_Common.isSelectedTime) {
            String[] parts = timeRangeStr.split(isEqual);
            if (parts.length < 4) continue;
            String fromTime = parts[0].trim();
            String toTime = parts[1].trim();
            int selectedSlotId;
            int isBooking;
            try {
                selectedSlotId = Integer.parseInt(parts[2].trim());
                isBooking = Integer.parseInt(parts[3].trim());
            } catch (NumberFormatException e) {
                continue;
            }

            if (isBooking != 1) {
                selectedIds.add(selectedSlotId);
                Util_Common.isSelectedSlotIds.add(selectedSlotId);
            }

            // For safety, we won't parse times here again for each selected string;
            // we'll use the slot objects below (selected slot objects from allSlotDetails)
        }

        // 3) For each selected id, find the corresponding SlotDetail (from allSlotDetails)
        //    and compare its time range with every other slot's time range
        for (Integer selId : selectedIds) {
            SlotDetail selectedSlot = null;
            for (SlotDetail possible : allSlotDetails) {
                if (possible.getSlot_id() == selId) {
                    selectedSlot = possible;
                    break;
                }
            }
            if (selectedSlot == null) continue;

            Date selFrom = timeFormat.parse(selectedSlot.getFrom_time());
            Date selTo = timeFormat.parse(selectedSlot.getTo_time());

            for (SlotDetail other : allSlotDetails) {
                if (other.getSlot_id() == selectedSlot.getSlot_id()) continue; // skip same slot

                Date otherFrom = timeFormat.parse(other.getFrom_time());
                Date otherTo = timeFormat.parse(other.getTo_time());

                // overlap condition
                if (selFrom.before(otherTo) && otherFrom.before(selTo)) {
                    // Do not mark myBooking or already-booked slots as overlapping to disable them,
                    // but we DO add their id to overlappingSlots so UI logic can check it if needed.
                    Util_Common.overlappingSlots.add(other.getSlot_id());
                }
            }
        }

        // 4) Apply overlap results: set isBooking=2 for overlapping slots (unless booked/my booking)
        for (SlotDetail slot : allSlotDetails) {
            boolean isOverlapping = Util_Common.overlappingSlots.contains(slot.getSlot_id());
            boolean isMyBooking = slot.getIsMyBooking() == 1;
            boolean isBookedByOther = slot.getIsBooking() == 1;

            if (isOverlapping && !isMyBooking && !isBookedByOther) {
                slot.setIsBooking(2); // disable overlapping slot
            } else if (!isOverlapping && !isMyBooking && !isBookedByOther) {
                // If not overlapping and not booked/my booking, make available
                slot.setIsBooking(0);
            }
            // If isMyBooking or isBookedByOther, leave their isBooking as-is
        }
    }

    public void setOnSlotSelectedListener(OnSlotSelectedListener listener) {
        this.onSlotSelectedListener = listener;
    }

    public interface OnSlotSelectedListener {
        void onSlotSelected(boolean isAnySlotSelected);
    }

    static class ViewHolder {
        TextView tvFromTime;
        RelativeLayout crdTiming;

        ViewHolder(View view) {
            tvFromTime = view.findViewById(R.id.lblTiming);
            crdTiming = view.findViewById(R.id.header);
        }
    }
}