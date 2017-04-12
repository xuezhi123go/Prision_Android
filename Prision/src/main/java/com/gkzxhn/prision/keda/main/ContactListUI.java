/**
 * @(#)ContactSearchList.java   2014-8-27
 * Copyright 2014  it.kedacom.com, Inc. All rights reserved.
 */

package com.gkzxhn.prision.keda.main;

import android.os.Bundle;
import android.widget.ListView;

import com.gkzxhn.prision.R;
import com.gkzxhn.prision.keda.callback.ImMtcCallback;
import com.gkzxhn.prision.keda.sky.app.base.PcActivity;


/**
  * 联系人搜索列表
  * 
  * @author chenj
  * @date 2014-8-27
  */

public class ContactListUI extends PcActivity {

	private ListView mListView;

	private ContactSearchListAdapter mAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact);
		onViewCreated();
	}

	public void findViews() {
		mListView = (ListView) findViewById(R.id.sListView);
	}

	public void initComponentValue() {

		mAdapter = new ContactSearchListAdapter(ContactListUI.this, ImMtcCallback.mContacts);
		mListView.setAdapter(mAdapter);

	}


	@Override
	public void registerListeners() {

	}

	/*	*//**
			* 更新列表
			* 
			* @param seachrKey
			* @param contacts
			*/
	/*
	public void updateListView(final String seachrKey, final int totalCount, final List<Contact> contacts) {
	if (TextUtils.isEmpty(seachrKey) || !StringUtils.isEquals(seachrKey, mCurrSearchTextStr)) {
		return;
	}

	runOnUiThread(new Runnable() {

		@Override
		public void run() {
			// 搜索一个e164为空时，显示出e164
			if ((null == contacts || contacts.isEmpty()) && ValidateUtils.isE164(seachrKey)) {
				Contact contact = new Contact();
				contact.setE164(seachrKey);
				contact.setMarkName(seachrKey);

				List<Contact> tmpContacts = new ArrayList<Contact>();
				tmpContacts.add(contact);
				mAdapter.setContacts(tmpContacts);
			} else {
				mAdapter.setContacts(contacts);
			}

			mAdapter.notifyDataSetChanged();
		}
	});
	}
	*/
}
