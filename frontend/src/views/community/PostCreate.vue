<template>
  <div class="post-create">
    <div class="container">
      <el-card shadow="hover">
        <div slot="header" class="card-header">
          <h2 class="page-title">{{ isEdit ? '编辑帖子' : '发布帖子' }}</h2>
        </div>
        
        <el-form :model="postForm" :rules="rules" ref="postForm" label-width="100px">
          <el-form-item label="帖子类型" prop="type">
            <el-radio-group v-model="postForm.type" size="large">
              <el-radio label="article" border>文章</el-radio>
              <el-radio label="question" border>问题</el-radio>
            </el-radio-group>
          </el-form-item>
          
          <el-form-item label="标题" prop="title">
            <el-input 
              v-model="postForm.title" 
              placeholder="请输入标题"
              size="large"
              maxlength="100"
              show-word-limit
            ></el-input>
          </el-form-item>
          
          <el-form-item label="分类" prop="category">
            <el-select 
              v-model="postForm.category" 
              placeholder="选择分类"
              size="large"
            >
              <el-option label="创业经验" value="创业经验"></el-option>
              <el-option label="融资技巧" value="融资技巧"></el-option>
              <el-option label="技术分享" value="技术分享"></el-option>
              <el-option label="行业动态" value="行业动态"></el-option>
              <el-option label="求职招聘" value="求职招聘"></el-option>
              <el-option label="活动公告" value="活动公告"></el-option>
              <el-option label="产品运营" value="产品运营"></el-option>
              <el-option label="市场营销" value="市场营销"></el-option>
              <el-option label="团队管理" value="团队管理"></el-option>
            </el-select>
          </el-form-item>
          
          <el-form-item label="标签" prop="tags">
            <el-input 
              v-model="postForm.tags" 
              placeholder="请输入标签，用逗号分隔"
              size="large"
            ></el-input>
          </el-form-item>
          
          <el-form-item label="内容" prop="content">
            <div class="editor-container">
              <el-input
                type="textarea"
                v-model="postForm.content"
                placeholder="请输入内容，支持富文本格式"
                :rows="12"
                size="large"
                maxlength="5000"
                show-word-limit
              ></el-input>
              
              <!-- 图片上传 -->
              <div class="upload-section">
                <el-upload
                  class="image-upload"
                  action="/api/files/upload"
                  :headers="{ Authorization: 'Bearer ' + $store.state.user.token }"
                  :on-success="handleImageUploadSuccess"
                  :on-error="handleImageUploadError"
                  :before-upload="beforeUpload"
                  multiple
                  list-type="picture-card"
                >
                  <i class="el-icon-plus"></i>
                  <div class="el-upload__text">上传图片</div>
                </el-upload>
                <div class="upload-tip">支持 JPG、PNG、GIF 格式，单张不超过 5MB</div>
              </div>
            </div>
          </el-form-item>
          
          <el-form-item>
            <el-button 
              type="primary" 
              size="large" 
              @click="submitForm"
              :loading="loading"
              :disabled="loading"
            >
              {{ isEdit ? '更新' : '发布' }}
            </el-button>
            <el-button 
              size="large" 
              @click="resetForm"
              :disabled="loading"
            >
              重置
            </el-button>
            <el-button 
              size="large" 
              @click="navigateBack"
              :disabled="loading"
            >
              取消
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { postAPI } from '@/api/community'

export default {
  name: 'PostCreate',
  data() {
    return {
      isEdit: false,
      postId: null,
      postForm: {
        type: 'article',
        title: '',
        category: '',
        tags: '',
        content: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 2, max: 100, message: '标题长度在 2 到 100 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' },
          { min: 10, message: '内容长度至少 10 个字符', trigger: 'blur' }
        ]
      },
      loading: false
    }
  },
  mounted() {
    // 检查是否登录
    if (!this.$store.state.user.token) {
      this.$message.warning('请先登录')
      this.$router.push('/login')
      return
    }
    
    // 检查是否是编辑模式
    const id = this.$route.params.id
    if (id) {
      this.isEdit = true
      this.postId = id
      this.loadPostDetail()
    }
  },
  methods: {
    loadPostDetail() {
      this.loading = true
      postAPI.getPostDetail(this.postId).then(res => {
        if (res.data) {
          this.postForm = res.data
        } else {
          this.$message.error('加载帖子详情失败')
          this.$router.push('/community')
        }
      }).catch(err => {
        console.error('加载帖子详情失败:', err)
        this.$message.error('加载帖子详情失败')
        this.$router.push('/community')
      }).finally(() => {
        this.loading = false
      })
    },
    submitForm() {
      this.$refs.postForm.validate((valid) => {
        if (valid) {
          this.loading = true
          if (this.isEdit) {
            // 编辑帖子
            postAPI.updatePost(this.postId, this.postForm).then(res => {
              if (res.code === 200) {
                this.$message.success('更新成功')
                this.$router.push(`/community/post/${this.postId}`)
              } else {
                this.$message.error(res.message || '更新失败')
              }
            }).catch(err => {
              console.error('更新帖子失败:', err)
              this.$message.error('更新失败，请稍后重试')
            }).finally(() => {
              this.loading = false
            })
          } else {
            // 发布帖子
            console.log('开始发布帖子，表单数据:', this.postForm)
            console.log('当前token:', localStorage.getItem('token'))
            postAPI.createPost(this.postForm).then(res => {
              console.log('发布帖子响应:', res)
              if (res === '发布成功') {
                this.$message.success('发布成功')
                this.$router.push('/community')
              } else if (res.code === 200) {
                this.$message.success('发布成功')
                this.$router.push('/community')
              } else {
                this.$message.error(res.message || '发布失败')
              }
            }).catch(err => {
              console.error('发布帖子失败:', err)
              console.error('错误响应:', err.response)
              this.$message.error('发布失败，请稍后重试')
            }).finally(() => {
              this.loading = false
            })
          }
        } else {
          return false
        }
      })
    },
    resetForm() {
      this.$refs.postForm.resetFields()
    },
    navigateBack() {
      this.$router.back()
    },
    // 图片上传相关方法
    beforeUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png' || file.type === 'image/gif'
      const isLt5M = file.size / 1024 / 1024 < 5
      
      if (!isJPG) {
        this.$message.error('上传图片只能是 JPG、PNG、GIF 格式!')
      }
      if (!isLt5M) {
        this.$message.error('上传图片大小不能超过 5MB!')
      }
      return isJPG && isLt5M
    },
    handleImageUploadSuccess(response, file, fileList) {
      console.log('图片上传响应:', response)
      if (response.code === 200 && response.data) {
        // 将图片URL插入到内容中
        const imageUrl = response.data
        const imageTag = `<img src="${imageUrl}" alt="图片" style="max-width: 100%; height: auto; margin: 10px 0;">`
        this.postForm.content += imageTag
        this.$message.success('图片上传成功')
      } else {
        this.$message.error('图片上传失败')
      }
    },
    handleImageUploadError(err, file, fileList) {
      this.$message.error('图片上传失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
.post-create {
  padding: 30px 0;
  min-height: calc(100vh - 120px);
}

.container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 0 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 24px;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1890ff;
  margin: 0;
}

/* 编辑器容器 */
.editor-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* 图片上传 */
.upload-section {
  margin-top: 8px;
}

.image-upload {
  margin-bottom: 8px;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-create {
    padding: 20px 0;
  }
  
  .container {
    padding: 0 15px;
  }
  
  .card-header {
    margin-bottom: 20px;
  }
  
  .page-title {
    font-size: 20px;
  }
  
  .editor-container {
    gap: 12px;
  }
  
  .image-upload {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .el-upload--picture-card {
    width: 80px;
    height: 80px;
  }
}
</style>