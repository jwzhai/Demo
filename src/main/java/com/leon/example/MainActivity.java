package com.leon.example;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.SimpleAdapter;

import com.leon.example.database.SimpleDataContract;
import com.leon.example.database.SimpleDataDbHelper;
import com.leon.example.fragment.NewItemFragment;
import com.leon.example.fragment.ToDoListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends ActionBarActivity implements NewItemFragment.OnNewItemAddedListener {
    // 内存列表数据对象引用
    private List<Map<String, Object>> mItems;
    // 列表适配器对象引用
    private SimpleAdapter mAdapter;
    // 数据库助手类对象引用
    private SimpleDataDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            /**
             * 范例程序，默认加载占位Fragment
             * */
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();

            /**
             * 初始化SQLiteOpenHelper对象
             * */
            mDbHelper = new SimpleDataDbHelper(this);

            /**
             * 方式一： 在activity_main.xml中添加<FrameLayout/>标签的“动态”方式添加Fragment
             * */
            FragmentManager fm = getSupportFragmentManager();
            // 启动FragmentTransaction事务
            FragmentTransaction ft = fm.beginTransaction();
            // 将NewItemFragment添加到Activity中
            ft.add(R.id.newItemContainer, new NewItemFragment());
            // 初始化ToDoListFragment对象
            ToDoListFragment toDoListFragment = new ToDoListFragment();
            mItems = new ArrayList<Map<String, Object>>();
            mAdapter = new SimpleAdapter(this,
                    mItems,
                    R.layout.simple_list_item_2,
                    new String[]{SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME, SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER},
                    new int[]{R.id.ListName, R.id.ListNumb});
            toDoListFragment.setListAdapter(mAdapter);
            // 启动 异步任务-1，从数据库的simple_entry表中读出初始数据
            new LoadDataAsyncTask(mDbHelper, mItems, mAdapter).execute();
            // 将ToDoListFragment添加到Activity中
            ft.add(R.id.todoListContainer, toDoListFragment);
            // 自定义动画
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
            // 提交变化
            ft.commit();


            /**
             * 方式二：在activity_main.xml中添加<fragment/>标签的“静态”方式添加Fragment
             * */
//            FragmentManager fm = getSupportFragmentManager();
//            ToDoListFragment toDoListFragment = (ToDoListFragment) fm.findFragmentById(R.id.ToDoListFragment);
//            mItems = new ArrayList<Map<String, Object>>();
//            for(int i=0; i<3; i++) {
//                Map<String, Object> keyValues = new HashMap<String, Object>();
//                keyValues.put("entry_name", "example");
//                keyValues.put("entry_number", i);
//                mItems.add(keyValues);
//            }
//            mAdapter = new SimpleAdapter(this,
//                    mItems,
//                    R.layout.simple_list_item_2,
//                    new String[]{"entry_name", "entry_number"},
//                    new int[]{R.id.ListName, R.id.ListNumb});
//            toDoListFragment.setListAdapter(mAdapter);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewItemAdded(String name, int number) {
        // 添加到内存对象
        Map<String, Object> keyValues = new HashMap<String, Object>();
        keyValues.put(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME, name);
        keyValues.put(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER, String.valueOf(number));
        mItems.add(keyValues);
        mAdapter.notifyDataSetChanged();
        // 启动 异步任务-2， 添加列表项数据到sqlite数据库
        new AddDataAsyncTask(mDbHelper).execute(keyValues);
    }

    /**
     * 异步任务-1 类：加载数据库的simple_entry表中的列表项到内存
     */
    public class LoadDataAsyncTask extends AsyncTask<Void, Integer, Void> {
        private SQLiteOpenHelper mDbHelper;
        private List<Map<String, Object>> mItems;
        private SimpleAdapter mAdapter;

        public LoadDataAsyncTask(SQLiteOpenHelper mDbHelper, List<Map<String, Object>> mItems, SimpleAdapter mAdapter) {
            this.mDbHelper = mDbHelper;
            this.mItems = mItems;
            this.mAdapter = mAdapter;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // 获取可读数据库对象
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            // 定义 视窗
            String[] projection = {
                    SimpleDataContract.SimpleEntry._ID,
                    SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME,
                    SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER
            };
            // 定义 “where”及参数域
            String selection = "";
            String[] selectionArgs = {};
            // 定义 降序排序
            String sortOrder = SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER + " DESC";
            // 获取查询请求的 返回指针
            Cursor cursor = db.query(
                    SimpleDataContract.SimpleEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
            // 遍历数据库表中的数据，并适配到内存对象mItems中
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // 添加到内存对象
                Map<String, Object> values = new HashMap<String, Object>();
                values.put(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME, cursor.getString(cursor.getColumnIndex(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME)));
                values.put(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER, cursor.getString(cursor.getColumnIndex(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER)));
                mItems.add(values);
            }
            cursor.close();
            db.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 异步任务-2 类：添加列表项到数据库的simple_entry表中
     */
    public class AddDataAsyncTask extends AsyncTask<Map<String, Object>, Integer, Long> {
        private SQLiteOpenHelper mDbHelper;

        public AddDataAsyncTask(SQLiteOpenHelper mDbHelper) {
            this.mDbHelper = mDbHelper;
        }

        @Override
        protected Long doInBackground(Map<String, Object>... params) {
            final Map<String, Object> keyValues = params[0];
            // 获取可写数据库对象
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            // 创建数据集Key-Value
            ContentValues values = new ContentValues();
            values.put(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME, (String) keyValues.get(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NAME));
            values.put(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER, (String) keyValues.get(SimpleDataContract.SimpleEntry.COLUMN_NAME_ENTRY_NUMBER));
            Long rowId = db.insert(
                    SimpleDataContract.SimpleEntry.TABLE_NAME,
                    null,
                    values);
            db.close();
            // 插入新行，返回row_id
            return rowId;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
