export interface SportCategory {
  value: string
  label: string
  icon: string
  description: string
}

export const SPORT_CATEGORIES: SportCategory[] = [
  { value: 'MMA', label: '综合格斗', icon: '🥊', description: 'MMA 训练手套、紧身衣、短裤、护齿等综合格斗装备' },
  { value: 'BOXING', label: '拳击装备', icon: '🥊', description: '拳击手套、绑手带、速度球、沙袋等拳击训练器材' },
  { value: 'KICKBOXING', label: '踢拳 · 泰拳', icon: '🦵', description: '泰拳护胫、拳击手套、护头等站立打击训练装备' },
  { value: 'BJJ', label: '柔术 · 摔跤', icon: '🤼', description: '巴西柔术道服、无道服训练服、护膝等 grappling 装备' },
  { value: 'PROTECTION', label: '防护装备', icon: '🛡️', description: '护齿、护裆、头部护具、护膝等全方位训练护具' },
  { value: 'FITNESS', label: '力量 · 体能', icon: '🏋️', description: '壶铃、跳绳、反应球、药球等体能训练器材' },
  { value: 'RECOVERY', label: '运动恢复', icon: '💆', description: '泡沫轴、筋膜枪、拉伸带等训练后恢复辅助工具' },
  { value: 'MERCH', label: '主题周边', icon: '👕', description: '格斗主题 T 恤、帽子、背包等综合格斗文化周边商品' }
]

const categoryMap = new Map(SPORT_CATEGORIES.map((item) => [item.value, item.label]))

export function getCategoryLabel(value?: string): string {
  if (!value) return '格斗装备'
  return categoryMap.get(value) ?? '格斗装备'
}
