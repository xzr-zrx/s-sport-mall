export interface SportCategory {
  value: string
  label: string
  icon: string
  description: string
}

export const SPORT_CATEGORIES: SportCategory[] = [
  { value: 'RUNNING', label: '跑步装备', icon: '🏃', description: '从日常慢跑到进阶训练，轻快迈出每一步' },
  { value: 'BASKETBALL', label: '篮球装备', icon: '🏀', description: '球鞋、篮球与训练装备，释放球场能量' },
  { value: 'FOOTBALL', label: '足球装备', icon: '⚽', description: '稳定控球与快速启动，为绿茵场而生' },
  { value: 'BADMINTON', label: '羽毛球装备', icon: '🏸', description: '轻量球拍与耐打用球，提升挥拍体验' },
  { value: 'FITNESS', label: '健身训练', icon: '🏋️', description: '力量、瑜伽与居家训练的实用选择' },
  { value: 'OUTDOOR', label: '户外运动', icon: '⛰️', description: '陪伴徒步、骑行和周末探索的可靠装备' },
  { value: 'PROTECTION', label: '运动护具', icon: '🛡️', description: '科学支撑关键部位，让训练更安心' },
  { value: 'SPORT_ACCESSORIES', label: '运动配件', icon: '🎽', description: '补齐运动细节，打造更完整的装备组合' }
]

const categoryMap = new Map(SPORT_CATEGORIES.map((item) => [item.value, item.label]))

export function getCategoryLabel(value?: string): string {
  if (!value) return '运动装备'
  return categoryMap.get(value) ?? '运动装备'
}
