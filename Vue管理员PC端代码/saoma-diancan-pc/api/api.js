
const url = 'http://localhost:81/'

const urls = class{
	static m(){
		// 注册接口
		const register = `${url}register/`
		// 管理员登录接口
		const adminLogin = `${url}adminLogin`
		// 统一登录接口
		const login = `${url}login`
		// 修改密码
		const updatePassword = `${url}admin/modifyPassword`
		// 获取门店信息
		const getShopInfo = `${url}admin/shopInfo`
		// 保存门店信息
		const saveShopInfo = `${url}admin/saveShopInfo`
		// 获取商家信息
		const obtainshop = `${url}obtainshop/`
		// 上传图片接口
		const uploadres = `${url}admin/dish/uploadImage`
		// 上传商家信息
		const uploadshop = `${url}uploadshop/`
		// 修改商家信息
		const modifyshop = `${url}modifyshop/`
		// 获取菜品类目
		const obtaincate = `${url}admin/category/list`
		// 获取所有类目
		const listAllcategory=`${url}admin/category/listAll`
		// 小程序端获取所有类目
		const categoryListAll=`${url}category/listAll`
		// 小程序端获取所有菜品
		const dishListAll=`${url}dish/listAll`
		// 添加类目
		const addcategory = `${url}admin/category/add`
		// 添加类目
		const deleteCategory = `${url}admin/category/delete`
		// 获取菜品
		const obtaindishes = `${url}admin/dish/list`
		// 获取菜品单位
		const obtainunit = `${url}admin/unit/listAll`
		// 添加菜品单位
		const dishunit = `${url}admin/unit/add`
		// 上架菜品
		const uploaddishes = `${url}admin/dish/save`
		// 删除菜品
		const dishDelete = `${url}admin/dish/delete`
		// 修改上架的菜品
		const modifydishes = `${url}modifydishes/`
		// 下架/上架菜品
		const fromsale = `${url}admin/dish/updateOnSale`
		// 批量上架菜品
		const batchOnSale = `${url}admin/dish/batchOnSale`
		// 批量下架菜品
		const batchOffSale = `${url}admin/dish/batchOffSale`
		// 获取订单
		const obtainorder = `${url}admin/order/list`

		// 查看详细菜单
		const vieworder = `${url}admin/order/vieworder`
		// 修改订单状态
		const updateStatus = `${url}admin/order/updateStatus`
		// 接单
		const receiving = `${url}admin/order/receiving`
		// 结账
		const checkout = `${url}admin/order/checkout`
		// 完成订单
		const complete = `${url}admin/order/complete`
		// 桌号管理
		const qrcode = `${url}admin/table/add`
		// 获取桌号
		const getqrcode = `${url}admin/table/list`
		// 删除桌号
		const tableDelete = `${url}admin/table/delete`
		// 批量生成所有餐桌二维码
		const generateAllQrCodes = `${url}admin/table/generateAllQrCodes`
		// 单独生成某个餐桌的二维码
		const generateQrCode = `${url}admin/table/generateQrCode`
		// 数据分析：七天销售额
		const salesvolume = `${url}admin/order/salesvolume`
		// 数据分析：热门菜品排行
		const hotDishes = `${url}admin/order/hotDishes`
		// 数据分析：实时经营数据
		const realTimeData = `${url}admin/order/realTimeData`
		// 服务员列表
		const waiterList = `${url}admin/waiter/list`
		// 服务员添加
		const waiterAdd = `${url}admin/waiter/add`
		// 服务员修改
		const waiterUpdate = `${url}admin/waiter/update`
		// 服务员删除
		const waiterDelete = `${url}admin/waiter/delete`
		// 服务员详情
		const waiterDetail = `${url}admin/waiter/detail`
		// 更新桌位状态
		const updateTableState = `${url}admin/table/updateState`
		// 服务请求列表
		const serviceRequestList = `${url}admin/waiter/serviceRequest/list`
		// 更新服务请求状态
		const updateServiceRequestState = `${url}admin/waiter/serviceRequest/updateState`
		// 反馈列表
		const feedbackList = `${url}admin/waiter/feedback/list`
		// 更新反馈状态
		const updateFeedbackState = `${url}admin/waiter/feedback/updateState`
		// 评论列表
		const reviewList = `${url}order-review/list`
		// 退菜
		const returnDish = `${url}admin/order/returnDish`
		// 导出订单Excel
		const exportOrder = `${url}admin/order/export`
		return{
			register,
			login,
			adminLogin,
			obtainshop,
			uploadres,
			uploadshop,
			modifyshop,
			obtaincate,
			listAllcategory,
			categoryListAll,
			dishListAll,
			deleteCategory,
			addcategory,
			obtaindishes,
			dishDelete,
			obtainunit,
			dishunit,
			uploaddishes,
			modifydishes,
			fromsale,
			batchOnSale,
			batchOffSale,
			obtainorder,
			vieworder,
			updateStatus,
			receiving,
			checkout,
			complete,
			qrcode,
			getqrcode,
			tableDelete,
			generateAllQrCodes,
			generateQrCode,
			salesvolume,
			hotDishes,
			realTimeData,
			updatePassword,
			getShopInfo,
			saveShopInfo,
			waiterList,
			waiterAdd,
			waiterUpdate,
			waiterDelete,
			waiterDetail,
			updateState: updateTableState,
			serviceRequestList,
			updateServiceRequestState,
			feedbackList,
			updateFeedbackState,
			reviewList,
			returnDish,
			exportOrder
		}
	}
}
export default urls
