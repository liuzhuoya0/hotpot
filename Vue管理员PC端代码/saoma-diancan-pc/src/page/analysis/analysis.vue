<template>
	<div class="ordering" v-loading.fullscreen.lock="loading">
		<div class="heading">经营数据分析</div>
		
		<div class="content-view">
			<div class="time-filter">
				<span class="filter-label">时间筛选：</span>
				<el-date-picker
					v-model="dateRange"
					type="daterange"
					range-separator="至"
					start-placeholder="开始日期"
					end-placeholder="结束日期"
					size="medium"
					@change="handleTimeChange"
					value-format="yyyy-MM-dd">
				</el-date-picker>
				<el-button type="primary" size="medium" @click="refreshData" class="refresh-btn">
					<i class="el-icon-refresh"></i> 刷新数据
				</el-button>
			</div>

			<div class="stats-cards">
				<div class="stat-card" v-for="(stat, index) in realTimeStats" :key="index" :style="{ '--delay': index * 0.1 + 's' }">
					<div class="stat-icon" :style="{ background: stat.color }">
						<i :class="stat.icon"></i>
					</div>
					<div class="stat-content">
						<div class="stat-value">{{ stat.value }}</div>
						<div class="stat-label">{{ stat.label }}</div>
					</div>
				</div>
			</div>

			<div class="charts-container">
				<div class="chart-card">
					<div class="chart-title">
						<span>{{ getChartTitle() }}</span>
					</div>
					<div id="container" class="chart-container"></div>
				</div>

				<div class="chart-card">
					<div class="chart-title">
						<span>热门菜品排行 TOP 10</span>
					</div>
					<div class="hot-dishes-list">
						<div class="dish-item" v-for="(dish, index) in hotDishesList" :key="index">
							<div class="dish-rank" :class="'rank-' + (index + 1)">
								{{ index + 1 }}
							</div>
							<div class="dish-image">
								<el-image 
									style="width: 60px; height: 60px; border-radius: 4px;"
									:src="getServerUrl()+'/image/dish/'+(dish.image || '')"
									fit="cover"
									:preview-src-list="[getServerUrl()+'/image/dish/'+(dish.image || '')]">
								</el-image>
							</div>
							<div class="dish-info">
								<div class="dish-name">{{ dish.name }}</div>
								<div class="dish-stats">
									<span class="stat-item">
										<i class="el-icon-shopping-cart-2"></i>
										{{ dish.totalQuantity }} 份
									</span>
									<span class="stat-item">
										<i class="el-icon-money"></i>
										¥{{ (dish.totalSales || 0).toFixed(2) }}
									</span>
								</div>
							</div>
							<div class="dish-progress">
								<div class="progress-bar" :style="{ width: getProgressWidth(dish, index) + '%' }"></div>
							</div>
						</div>
						<div class="no-data" v-if="hotDishesList.length === 0">
							暂无菜品数据
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</template>

<script>
import { Column } from '@antv/g2plot';
import { getServerUrl } from '../../../config/sys.js';

export default {
	data() {
		return {
			loading: true,
			dateRange: [],
			realTimeStats: [
				{ label: '总订单数', value: 0, icon: 'el-icon-s-order', color: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' },
				{ label: '已支付', value: 0, icon: 'el-icon-circle-check', color: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' },
				{ label: '待支付', value: 0, icon: 'el-icon-time', color: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)' },
				{ label: '进行中', value: 0, icon: 'el-icon-loading', color: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)' },
				{ label: '已完成', value: 0, icon: 'el-icon-s-claim', color: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)' },
				{ label: '总营收', value: '¥0', icon: 'el-icon-wallet', color: 'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)' }
			],
			hotDishesList: [],
			columnPlot: null
		}
	},
	methods: {
		getServerUrl() {
			return getServerUrl();
		},
		getProgressWidth(dish, index) {
			if (this.hotDishesList.length === 0) return 0;
			const maxQuantity = (this.hotDishesList[0] && this.hotDishesList[0].totalQuantity) || 1;
			return (dish.totalQuantity / maxQuantity) * 100;
		},
		getChartTitle() {
			if (this.dateRange && this.dateRange.length === 2) {
				return `${this.dateRange[0]} 至 ${this.dateRange[1]} 销售额`;
			}
			return '销售额';
		},
		async fetchRealTimeData() {
			try {
				let url = this.Urls.m().realTimeData;
				if (this.dateRange && this.dateRange.length === 2) {
					url += `?startDate=${this.dateRange[0]}&endDate=${this.dateRange[1]}`;
				}
				let res = await new this.Request(url).modeget();
				const data = res.data.realTimeData;
				this.realTimeStats[0].value = data.totalOrders || 0;
				this.realTimeStats[1].value = data.paidOrders || 0;
				this.realTimeStats[2].value = data.unpaidOrders || 0;
				this.realTimeStats[3].value = data.ongoingOrders || 0;
				this.realTimeStats[4].value = data.completedOrders || 0;
				this.realTimeStats[5].value = '¥' + (data.totalRevenue || 0).toFixed(2);
			} catch (e) {
				console.error('获取实时数据失败:', e);
			}
		},
		async fetchHotDishes() {
			try {
				let url = this.Urls.m().hotDishes;
				if (this.dateRange && this.dateRange.length === 2) {
					url += `?startDate=${this.dateRange[0]}&endDate=${this.dateRange[1]}`;
				}
				let res = await new this.Request(url).modeget();
				this.hotDishesList = res.data.hotDishList || [];
			} catch (e) {
				console.error('获取热门菜品失败:', e);
			}
		},
		async seven_days() {
			try {
				let url = this.Urls.m().salesvolume;
				if (this.dateRange && this.dateRange.length === 2) {
					url += `?startDate=${this.dateRange[0]}&endDate=${this.dateRange[1]}`;
				}
				let res = await new this.Request(url).modeget();
				this.house(res.data.salesVolumeList);
			} catch (e) {
				new this.mytitle(this.$message, 'error', '服务器发生错误,请重试').funtitle();
			}
		},
		house(data) {
			if (this.columnPlot) {
				this.columnPlot.destroy();
			}
			
			const reversedData = [...data].reverse();
			
			this.columnPlot = new Column('container', {
				data: reversedData,
				xField: 'time',
				yField: 'sales_volume',
				height: 350,
				padding: [40, 50, 60, 60],
				label: {
					position: 'middle',
					style: {
						fill: '#FFFFFF',
						opacity: 0.9,
						fontSize: 12,
						fontWeight: 500
					}
				},
				xAxis: {
					label: {
						autoHide: true,
						autoRotate: false,
						style: {
							fontSize: 12,
							fill: '#64748b'
						}
					},
					line: {
						style: {
							stroke: '#e2e8f0'
						}
					},
					tickLine: null
				},
				yAxis: {
					label: {
						style: {
							fontSize: 12,
							fill: '#64748b'
						},
						formatter: (v) => '¥' + v
					},
					grid: {
						line: {
							style: {
								stroke: '#f1f5f9',
								lineDash: [4, 4]
							}
						}
					},
					line: null
				},
				meta: {
					time: {
						alias: '日期'
					},
					sales_volume: {
						alias: '销售额'
					}
				},
				color: {
					type: 'linear',
					values: ['#3b82f6', '#8b5cf6']
				},
				columnStyle: {
					radius: [8, 8, 0, 0]
				},
				tooltip: {
					showMarkers: false,
					domStyles: {
						'g2-tooltip': {
							backgroundColor: 'rgba(255, 255, 255, 0.95)',
							borderRadius: '12px',
							boxShadow: '0 4px 20px rgba(0, 0, 0, 0.15)',
							padding: '12px 16px'
						},
						'g2-tooltip-title': {
							color: '#1e293b',
							fontWeight: 600,
							marginBottom: '8px'
						},
						'g2-tooltip-list-item': {
							color: '#64748b'
						}
					}
				},
				animation: {
					appear: {
						animation: 'path-in',
						duration: 1000
					}
				}
			});
			this.columnPlot.render();
		},
		handleTimeChange() {
			this.seven_days();
		},
		async refreshData() {
			this.loading = true;
			await Promise.all([
				this.fetchRealTimeData(),
				this.fetchHotDishes(),
				this.seven_days()
			]);
			this.loading = false;
		},
		getDefaultDateRange() {
			const end = new Date();
			const start = new Date();
			start.setDate(start.getDate() - 6);
			return [
				this.formatDate(start),
				this.formatDate(end)
			];
		},
		formatDate(date) {
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		async init() {
			try {
				this.dateRange = this.getDefaultDateRange();
				await Promise.all([
					this.fetchRealTimeData(),
					this.fetchHotDishes(),
					this.seven_days()
				]);
			} catch (e) {
				console.error('初始化数据失败:', e);
			} finally {
				this.loading = false;
			}
		}
	},
	mounted() {
		this.init();
	},
	beforeDestroy() {
		if (this.columnPlot) {
			this.columnPlot.destroy();
		}
	}
}
</script>

<style scoped="scoped">
.ordering {
	background: linear-gradient(135deg, #f5f7fa 0%, #e4e8ed 100%);
	min-height: 100vh;
	padding: 20px;
}

.heading {
	font-size: 28px;
	font-weight: 700;
	color: #1e293b;
	margin: 0 20px 30px 20px;
	padding-left: 16px;
	border-left: 4px solid #2563eb;
	letter-spacing: -0.5px;
}

.content-view {
	background: #ffffff;
	border-radius: 16px;
	padding: 30px;
	margin: 0 20px;
	box-shadow: 0 10px 40px rgba(0, 0, 0, 0.08);
}

.time-filter {
	display: flex;
	align-items: center;
	margin-bottom: 30px;
	padding: 20px;
	background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
	border-radius: 12px;
}

.filter-label {
	font-size: 15px;
	color: #475569;
	font-weight: 500;
	margin-right: 12px;
}

.refresh-btn {
	margin-left: 16px;
	background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
	border: none;
	font-weight: 500;
}

.refresh-btn:hover {
	background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
	transform: translateY(-2px);
	box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
}

.stats-cards {
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
	gap: 20px;
	margin-bottom: 30px;
}

.stat-card {
	display: flex;
	align-items: center;
	padding: 24px;
	background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
	border-radius: 16px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	animation: slideInUp 0.6s ease-out forwards;
	opacity: 0;
	animation-delay: var(--delay);
}

@keyframes slideInUp {
	from {
		opacity: 0;
		transform: translateY(30px);
	}
	to {
		opacity: 1;
		transform: translateY(0);
	}
}

.stat-card:hover {
	transform: translateY(-4px);
	box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

.stat-icon {
	width: 60px;
	height: 60px;
	border-radius: 14px;
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 20px;
	flex-shrink: 0;
}

.stat-icon i {
	font-size: 28px;
	color: #ffffff;
}

.stat-content {
	flex: 1;
}

.stat-value {
	font-size: 32px;
	font-weight: 700;
	color: #1e293b;
	line-height: 1.2;
	margin-bottom: 4px;
}

.stat-label {
	font-size: 14px;
	color: #64748b;
	font-weight: 500;
}

.charts-container {
	display: grid;
	grid-template-columns: 1.2fr 1fr;
	gap: 24px;
}

.chart-card {
	background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);
	border-radius: 16px;
	padding: 24px;
	box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
}

.chart-title {
	font-size: 18px;
	font-weight: 600;
	color: #1e293b;
	margin-bottom: 20px;
	padding-bottom: 12px;
	border-bottom: 2px solid #e2e8f0;
	display: flex;
	align-items: center;
	justify-content: space-between;
}

.chart-title span {
	display: flex;
	align-items: center;
	gap: 8px;
}

.chart-title span::before {
	content: '';
	width: 4px;
	height: 18px;
	background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
	border-radius: 2px;
}

.chart-container {
	width: 100%;
}

.hot-dishes-list {
	max-height: 480px;
	overflow-y: auto;
}

.hot-dishes-list::-webkit-scrollbar {
	width: 6px;
}

.hot-dishes-list::-webkit-scrollbar-track {
	background: #f1f5f9;
	border-radius: 3px;
}

.hot-dishes-list::-webkit-scrollbar-thumb {
	background: linear-gradient(135deg, #3b82f6 0%, #8b5cf6 100%);
	border-radius: 3px;
}

.dish-item {
	display: flex;
	align-items: center;
	padding: 16px;
	margin-bottom: 12px;
	background: #ffffff;
	border-radius: 12px;
	transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
	border: 1px solid #f1f5f9;
}

.dish-item:hover {
	transform: translateX(4px);
	box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
	border-color: #e2e8f0;
}

.dish-rank {
	width: 36px;
	height: 36px;
	border-radius: 10px;
	display: flex;
	align-items: center;
	justify-content: center;
	font-weight: 700;
	font-size: 16px;
	margin-right: 16px;
	flex-shrink: 0;
	background: linear-gradient(135deg, #e2e8f0 0%, #cbd5e1 100%);
	color: #64748b;
}

.rank-1 {
	background: linear-gradient(135deg, #ffd700 0%, #ffb347 100%);
	color: #ffffff;
	box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
}

.rank-2 {
	background: linear-gradient(135deg, #c0c0c0 0%, #a8a8a8 100%);
	color: #ffffff;
	box-shadow: 0 4px 12px rgba(192, 192, 192, 0.4);
}

.rank-3 {
	background: linear-gradient(135deg, #cd7f32 0%, #b87333 100%);
	color: #ffffff;
	box-shadow: 0 4px 12px rgba(205, 127, 50, 0.4);
}

.dish-image {
	display: flex;
	align-items: center;
	justify-content: center;
	margin-right: 16px;
	flex-shrink: 0;
}

.dish-info {
	flex: 1;
	min-width: 0;
	margin-right: 12px;
}

.dish-name {
	font-size: 15px;
	font-weight: 600;
	color: #1e293b;
	margin-bottom: 8px;
	white-space: nowrap;
	overflow: hidden;
	text-overflow: ellipsis;
}

.dish-stats {
	display: flex;
	gap: 16px;
}

.stat-item {
	display: flex;
	align-items: center;
	gap: 4px;
	font-size: 13px;
	color: #64748b;
	font-weight: 500;
}

.stat-item i {
	font-size: 14px;
	color: #3b82f6;
}

.dish-progress {
	width: 80px;
	height: 6px;
	background: #f1f5f9;
	border-radius: 3px;
	overflow: hidden;
	flex-shrink: 0;
}

.progress-bar {
	height: 100%;
	background: linear-gradient(90deg, #3b82f6 0%, #8b5cf6 100%);
	border-radius: 3px;
	transition: width 0.8s cubic-bezier(0.4, 0, 0.2, 1);
}

.no-data {
	text-align: center;
	padding: 60px 20px;
	color: #94a3b8;
	font-size: 15px;
}

@media (max-width: 1200px) {
	.charts-container {
		grid-template-columns: 1fr;
	}
	
	.stats-cards {
		grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
	}
}

@media (max-width: 768px) {
	.content-view {
		padding: 20px;
		margin: 0 10px;
	}
	
	.heading {
		font-size: 24px;
		margin: 0 10px 20px 10px;
	}
	
	.time-filter {
		flex-wrap: wrap;
		gap: 12px;
	}
	
	.stats-cards {
		grid-template-columns: 1fr;
	}
	
	.stat-card {
		padding: 20px;
	}
	
	.stat-value {
		font-size: 28px;
	}
}
</style>
