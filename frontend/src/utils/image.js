export function getImageUrl(imageName) {
  if (!imageName) return ''
  // 如果已经是完整URL，直接返回
  if (imageName.startsWith('http://') || imageName.startsWith('https://')) {
    return imageName
  }
  // 如果是后端返回的API路径，直接使用
  if (imageName.startsWith('/api/')) {
    return imageName
  }
  // 检查是否是已知的占位图片
  const placeholderImages = ['1.png', '2.png', '3.png']
  if (placeholderImages.includes(imageName)) {
    return `/images/${imageName}`
  }
  // 返回空字符串，让后续逻辑使用默认占位图片
  return ''
}
