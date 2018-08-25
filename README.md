##RecycleBauble—— 基于Android RecyclerView的封装 ##

参考XRecyclerView源码进行修改和自定义。

 1. **下拉刷新 RefreshHeader、上拉加载 LoadFooter**
	利用RecyclerView的多布局对下拉刷新、上拉加载进行封装。可通过一下api设置是否使用。两者默认为使用。
	

    public void setRefreshEnabled(boolean refreshEnabled);
    public void setLoadEnabled(boolean loadEnabled);
    
	手动设置开启或关闭下拉加载：
	
	public void setRefresh(boolean isRefresh);

	列表滑动到底部自动进行加载操作，手动设置加载完成，参数为是否暂无更多数据：

	public void setLoadCompelete(boolean isNoMore);

	下拉刷新时出错，无法刷新数据可调用：
	
	public void setRefreshError();

	可自定义两个部分的View，需要继承库中的BaseRefreshView、BaseLoadView两个类：

	public void setRefreshView(BaseRefreshView refreshView);
	public void setLoadView(BaseLoadView loadView);

注：若适配器继承本库中的Adapter< Data >类，调用类中的api更新数据，无需手动调用上述刷新和加载完成的api。
![https://gitee.com/XiaoChuYang/RecycleBauble/blob/master/image/S80825-21273945_1_.gif]
![https://gitee.com/XiaoChuYang/RecycleBauble/blob/master/image/S80825-21504234_1_.gif]

 2. **空数据布局 EmptyView**
	 设置空数据布局：
	 
    public void setEmptyView(View emptyView);
    
 3. **Adapter**
	 本库对于Adpater进行封装——Adapter< Data > ，继承该类，默认重写以下方法：
	 
    protected abstract int getItemViewType(int position,Data data);

	该方法默认提供当前item的位置及该位置所对应的对象值，用户需将该item的布局id作为返回值返回。

    protected abstract ViewHolder< Data > onCreateViewHolder(View view,int viewType);

	该方法默认提供当前item的view以及对应的viewType，用户需要返回该viewType所对应的viewholder对象。

 4. **ViewHolder**
	 本库对于Adpater进行封装——ViewHolder< Data > ，继承该类默认重写以下方法：
	 
    protected abstract void onBind(Data data,int position);

	该方法是提供当前item的数据值以及位置，用户需要在此方法设置item的内容与数据绑定。

 5. **ItemDecoration**
	 本库对ItemDecoration作简易封装，分别为分割线以及等间距的设置。
	 DividerItemDecoration——分割线的封装：
	 使用以下方式设置：
	 
	 addItemDecoration(new DividerItemDecoration(this));

	重载的构造方法：

    public DividerItemDecoration(Context context);
    public DividerItemDecoration(Context context, int dividerHeight)；
    public DividerItemDecoration(Context context, int dividerHeight, @ColorRes int dividerColor);

	SpacesItemDecoration——等间距的封装：
	

    addItemDecoration(new SpacesItemDecoration(40,30))；

	构造方法：
	
    public SpacesItemDecoration(int topBottomSpace, int rightLeftSpace)；