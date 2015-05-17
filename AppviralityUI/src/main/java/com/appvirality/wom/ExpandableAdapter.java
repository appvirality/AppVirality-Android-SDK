package com.appvirality.wom;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.TextView;

import com.appvirality.android.AppviralityAPI;

public class ExpandableAdapter extends BaseExpandableListAdapter {

	private LayoutInflater layoutInflater;
	private Button btnInviteFriends;
	private LinkedHashMap<Parent, ArrayList<Contact>> groupList;
	private ArrayList<Parent> mainGroup;
	int totalSelected = 0, sizeOfRecentList;
	private static int childLayoutResourceId = -1, childTitleResourceId = -1, childCheckBoxResourceId = -1;
	private static int parentLayoutResourceId = -1, parentTitleResourceId = -1, parentCheckBoxResourceId = -1, parentImageResourceId;

	private static List<String> selectedPhoneNumbers= new ArrayList<String>();
	private static List<String> selectedEmails = new ArrayList<String>();
	private static List<String> selectedContacts= new ArrayList<String>();
	ExpandableListView expdListView;

	private Context cont;
	private Button btnSMS, btnEmails;

	protected static void setChildLayoutResouces(int childLayoutId, int childTitleId, int childCheckBoxId)
	{
		childLayoutResourceId = childLayoutId;
		childTitleResourceId = childTitleId;
		childCheckBoxResourceId = childCheckBoxId;
	}

	protected static void setParentLayoutResources(int parentLayoutId, int parentTitleId, int parentCheckBoxId, int parentImageId)
	{
		parentLayoutResourceId = parentLayoutId;
		parentTitleResourceId = parentTitleId;
		parentCheckBoxResourceId = parentCheckBoxId;
		parentImageResourceId = parentImageId;
	}

	public ExpandableAdapter(Context context, Button btnInvite, ExpandableListView listView, LinkedHashMap<Parent, 
			ArrayList<Contact>> groupsList, int contactList, boolean clearsaved, Button btnSms, Button btnEmail) 
	{
		layoutInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.btnInviteFriends = btnInvite;		
		cont = context;
		sizeOfRecentList = contactList;
		this.expdListView = listView;
		this.btnSMS = btnSms;
		this.btnEmails = btnEmail;

		listView.setOnGroupClickListener(new OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) { 
				return parent.isGroupExpanded(groupPosition);
			}
		});

		listView.setOnGroupExpandListener(new OnGroupExpandListener() {
			public void onGroupExpand(int groupPosition) {
				Contact contact = getChild(groupPosition, 0);
				contact.isChecked = true;
				if(contact.isEmail) {
					if(!selectedEmails.contains(contact.number))
						selectedEmails.add(contact.number);
				}
				else {
					if(!selectedPhoneNumbers.contains(contact.number.replaceAll("-", "")))
						selectedPhoneNumbers.add(contact.number.replaceAll("-", ""));
				}							
			}
		});

		if(clearsaved)
			clearSavedData();
		mainGroup = new ArrayList<Parent>();
		groupList = new LinkedHashMap<Parent, ArrayList<Contact>>();
		for (Map.Entry<Parent, ArrayList<Contact>> mapEntry : groupsList.entrySet()) 
		{
			Parent groupItem = mapEntry.getKey();
			groupItem.isChecked = selectedContacts.contains(groupItem.name);

			ArrayList<Contact> list = mapEntry.getValue();
			for(int i = 0; i < list.size(); i++)
				if(groupItem.isChecked || selectedPhoneNumbers.contains(list.get(i).number) || selectedEmails.contains(list.get(i).number))
					list.get(i).isChecked = true;	
				else
					list.get(i).isChecked = false;

			mainGroup.add(groupItem);
			groupList.put(groupItem, list);
		}

		btnSMS.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {				
				if(selectedPhoneNumbers.size() > 0) {
					AppviralityAPI.startActvity("com.android.contacts", null, cont, null, selectedPhoneNumbers);
				}
			}
		});

		btnEmails.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {
				if(selectedEmails.size() > 0) {
					AppviralityAPI.startActvity("com.android.contacts", null, cont, selectedEmails, null);
				}
			}
		});

		btnInviteFriends.setOnClickListener(new View.OnClickListener() {			
			public void onClick(View v) {

				if((selectedPhoneNumbers.size() == 0 && selectedEmails.size() == 0 && btnSMS.getVisibility() == View.VISIBLE) || selectedPhoneNumbers.size() > 0 && selectedEmails.size() > 0) {
					if(btnSMS.getVisibility() == View.VISIBLE) {
						btnSMS.setVisibility(View.GONE);
						btnEmails.setVisibility(View.GONE);

						if(selectedPhoneNumbers.size() + selectedEmails.size() > 0)
							btnInviteFriends.setText("Send Invites " + "(" + (selectedPhoneNumbers.size() + selectedEmails.size()) + ")");
						else
							btnInviteFriends.setText("Send Invites");
					}
					else {
						btnSMS.setVisibility(View.VISIBLE);
						btnEmails.setVisibility(View.VISIBLE);
						btnInviteFriends.setText("Send Invites");
					}
				}
				else					
					if(selectedPhoneNumbers.size() > 0) {
						AppviralityAPI.startActvity("com.android.contacts", null, cont, null, selectedPhoneNumbers);
					}
					else
						if(selectedEmails.size() > 0) {
							AppviralityAPI.startActvity("com.android.contacts", null, cont, selectedEmails, null);
						}
			}
		});
	}

	public static void clearSavedData()
	{
		selectedPhoneNumbers.clear();
		selectedEmails.clear();
		selectedContacts.clear();
	}

	public Contact getChild(int groupPosition, int childPosition) {
		Parent item = mainGroup.get(groupPosition);

		return groupList.get(item).size() > 0 ? groupList.get(item).get(childPosition) : new Contact();
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
		final ChildHolder holder;
		final Contact child = getChild(groupPosition, childPosition);
		if (view == null) {
			view = layoutInflater.inflate(childLayoutResourceId, null);
			holder = new ChildHolder();
			holder.cb = (CheckBox) view.findViewById(childCheckBoxResourceId);
			holder.title = (TextView) view.findViewById(childTitleResourceId);
			view.setTag(holder);
		} else {
			holder = (ChildHolder) view.getTag();
		}
		holder.cb.setOnCheckedChangeListener(null);

		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				holder.cb.setChecked(!holder.cb.isChecked());
			}
		});

		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(child.isEmail) {
					if(isChecked) {
						if(!selectedEmails.contains(child.number))
							selectedEmails.add(child.number);
					}
					else
						selectedEmails.remove(child.number);
				}
				else {
					if(isChecked) {
						if(!selectedPhoneNumbers.contains(child.number.replaceAll("-", "")))
							selectedPhoneNumbers.add(child.number.replaceAll("-", ""));
					}
					else
						selectedPhoneNumbers.remove(child.number.replaceAll("-", ""));
				}
				child.isChecked = isChecked;
				notifyDataSetChanged();
			}
		});
		holder.cb.setChecked(child.isChecked);
		holder.title.setText(child.number);
		if(isLastChild) {			
			setSelectedItems();
		}
		return view;
	}

	public int getChildrenCount(int groupPosition) {
		Parent item = mainGroup.get(groupPosition);
		return groupList.get(item).size();
	}

	public Parent getGroup(int groupPosition) {
		return mainGroup.get(groupPosition);
	}

	public int getGroupCount() {
		return mainGroup.size();
	}

	public int getItemViewType(int groupPosition) {
		return (groupList.get(mainGroup.get(groupPosition)).size() == 0) ? 0 : 1;
	}

	public long getGroupId(int groupPosition) {
		return 0;
	}

	public View getGroupView(final int groupPosition, boolean isExpanded, View view, ViewGroup parent)
	{
		final Parent groupItem = getGroup(groupPosition);
		final GroupHolder holder;
		int type = getItemViewType(groupPosition);
		if (view == null) {
			view = layoutInflater.inflate(parentLayoutResourceId, null);
			holder = new GroupHolder();
			holder.cb = (CheckBox) view.findViewById(parentCheckBoxResourceId);
			holder.title = (TextView) view.findViewById(parentTitleResourceId);
			holder.image = (RoundedImageView) view.findViewById(parentImageResourceId);
			view.setTag(holder);			
		} else {
			holder = (GroupHolder) view.getTag();
		}

		holder.cb.setTag(groupPosition);
		holder.cb.setOnCheckedChangeListener(null);
		if(type == 0) {
			holder.cb.setVisibility(View.GONE);
			holder.image.setVisibility(View.GONE);
			holder.title.setText(groupItem.name);
			holder.title.setGravity(Gravity.CENTER);

			holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
			DisplayMetrics displayMetrics = cont.getResources().getDisplayMetrics();
			int pd1 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, displayMetrics);
			int pd2 = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, displayMetrics);
			holder.title.setPadding(pd1, pd2, pd1, pd2);

			view.setBackgroundColor(Color.parseColor("#D8D8D8"));
			view.setTag(holder);						
			return view;
		}
		else {
			if(isExpanded)
				holder.cb.setVisibility(View.GONE);
			else
				holder.cb.setVisibility(View.VISIBLE);
			holder.title.setGravity(Gravity.LEFT);
			holder.title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
			holder.image.setVisibility(View.VISIBLE);	
			view.setBackgroundColor(Color.parseColor("#f5f5f5"));
			holder.title.setPadding(0, 0, 0, 0);
		}
		holder.title.setText(groupItem.name);
		holder.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int groupPos = Integer.parseInt(buttonView.getTag().toString());

				expdListView.expandGroup(groupPos);
			}
		});

		holder.cb.setChecked(groupItem.isChecked);
		if(groupItem.image != null)
			holder.image.setImageBitmap(groupItem.image);
		return view;
	}

	private void setSelectedItems() {
		if(selectedPhoneNumbers.size() + selectedEmails.size() > 0 && btnSMS.getVisibility() == View.GONE)
			btnInviteFriends.setText("Send Invites " + "(" + (selectedPhoneNumbers.size() + selectedEmails.size()) + ")");
		else
			btnInviteFriends.setText("Send Invites");

		btnSMS.setText("Invite via SMS (" + (selectedPhoneNumbers.size()) + ")");
		btnEmails.setText("Invite via Email (" + (selectedEmails.size()) + ")");
	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private class GroupHolder {
		public RoundedImageView image;
		public CheckBox cb;
		public TextView title;

	}

	private class ChildHolder {
		public TextView title;
		public CheckBox cb;
	}
}


