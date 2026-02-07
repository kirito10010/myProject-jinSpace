// 错误处理工具，用于过滤掉特定的错误消息

// 被过滤的错误关键词列表
const filteredErrorKeywords = [
  '音乐',
  'song',
  'lyric',
  'mv',
  'comment',
  'api.php',
  'zhyunxi.com'
]

// 存储被过滤的错误，用于调试
const suppressedErrors: Array<{ error: any; timestamp: number }> = []

// 保存原始的console方法
const originalConsoleError = console.error
const originalConsoleWarn = console.warn

// 重写console.error方法
console.error = function(...args: any[]) {
  // 检查错误是否应该被过滤
  const shouldSuppress = args.some(arg => {
    if (typeof arg === 'string') {
      return filteredErrorKeywords.some(keyword => arg.includes(keyword))
    }
    if (arg instanceof Error && arg.message) {
      return filteredErrorKeywords.some(keyword => arg.message.includes(keyword))
    }
    return false
  })
  
  if (shouldSuppress) {
    // 存储被过滤的错误
    suppressedErrors.push({
      error: args,
      timestamp: Date.now()
    })
    // 限制存储的错误数量，避免内存泄漏
    if (suppressedErrors.length > 100) {
      suppressedErrors.shift()
    }
  } else {
    // 对于不应该被过滤的错误，调用原始的console.error方法
    originalConsoleError.apply(console, args)
  }
}

// 重写console.warn方法（可选，根据需要）
console.warn = function(...args: any[]) {
  // 检查警告是否应该被过滤
  const shouldSuppress = args.some(arg => {
    if (typeof arg === 'string') {
      return filteredErrorKeywords.some(keyword => arg.includes(keyword))
    }
    return false
  })
  
  if (!shouldSuppress) {
    // 对于不应该被过滤的警告，调用原始的console.warn方法
    originalConsoleWarn.apply(console, args)
  }
}

// 导出一个方法，用于获取被过滤的错误（用于调试）
export function getSuppressedErrors() {
  return suppressedErrors
}

// 导出一个方法，用于清空被过滤的错误
export function clearSuppressedErrors() {
  suppressedErrors.length = 0
}

// 导出一个方法，用于临时禁用错误过滤（用于调试）
export function disableErrorFilter() {
  console.error = originalConsoleError
  console.warn = originalConsoleWarn
}

// 导出一个方法，用于重新启用错误过滤
export function enableErrorFilter() {
  // 重新应用我们的过滤逻辑
  console.error = function(...args: any[]) {
    const shouldSuppress = args.some(arg => {
      if (typeof arg === 'string') {
        return filteredErrorKeywords.some(keyword => arg.includes(keyword))
      }
      if (arg instanceof Error && arg.message) {
        return filteredErrorKeywords.some(keyword => arg.message.includes(keyword))
      }
      return false
    })
    
    if (shouldSuppress) {
      suppressedErrors.push({
        error: args,
        timestamp: Date.now()
      })
      if (suppressedErrors.length > 100) {
        suppressedErrors.shift()
      }
    } else {
      originalConsoleError.apply(console, args)
    }
  }
  
  console.warn = function(...args: any[]) {
    const shouldSuppress = args.some(arg => {
      if (typeof arg === 'string') {
        return filteredErrorKeywords.some(keyword => arg.includes(keyword))
      }
      return false
    })
    
    if (!shouldSuppress) {
      originalConsoleWarn.apply(console, args)
    }
  }
}
