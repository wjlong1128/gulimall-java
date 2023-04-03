<template>
    <div class="box">
        <el-upload v-model:file-list="fileList" class="upload-demo" action multiple :on-preview="handlePreview"
            :on-remove="handleRemove" :before-remove="beforeRemove" :limit="3" :on-exceed="handleExceed"
            :on-change="changeHandler">
            <el-button type="primary">Click to upload</el-button>
            <template #tip>
                <div class="el-upload__tip">
                    请上传文件
                </div>
            </template>
        </el-upload>
        <hr>
        <el-progress :text-inside="true" :stroke-width="26" :percentage="70" />
    </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import type { UploadProps, UploadUserFile, UploadFile, UploadFiles, UploadRawFile } from 'element-plus'
import SparkMD5 from 'spark-md5';
import axios, { AxiosResponse } from 'axios'
import { stringifyExpression } from '@vue/compiler-core';
interface ReqFile {
    chunk: Blob,
    name: number
}
const getMd5 = (buffer: string): string => {
    const sp = new SparkMD5()
    sp.appendBinary(buffer)
    return sp.end();
}

const changeHandler = async (uploadFile: UploadFile, uploadFiles: UploadFiles) => {
    const raw = uploadFile.raw as UploadRawFile
    console.log(raw);
    const buffer = await fileToBuffer(raw as UploadRawFile)
    const hash = getMd5(buffer)
    const { data } = await axios.get(`http://localhost:10001/file-service/bigfile/check?fileMd5=${hash}`)
    if (data.data.exist) {
        // 提示... 获取最新地址就ok
        return
    }


    // 没有就接着分片
    const chunkList: ReqFile[] = getChunkList(raw)
    const ps = sendRequestChunkList(chunkList, hash)
    // 这里直接执行上传
    try {
        ; (async () => {
            // result 就是执行检索且上传之后的对象
            const results = await Promise.all(ps);
            // console.log(results);
            for (let i = 0; i < results.length; i++) {
                if (!results[i].data.data.exist) {
                    throw new Error('文件上传失败')
                }

            }

            // 一定要在之后
            await axios.get('http://localhost:10001/file-service/bigfile/mergefile', {
                params: {
                    name: raw.name,
                    fileMd5: hash,
                    chunkSize: chunkList.length
                }
            }).then(res => {
                ElMessage.success('上传成功')
            })
        })()
        // 这里提示合并
    } catch (error) {
        // 执行回滚清楚操作
    }

}
// 将文件读取为 字符串 //字节数组
const fileToBuffer = (file: UploadRawFile) => {
    return new Promise<string>((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = e => {
            resolve(e.target!.result as string)
        }
        reader.readAsBinaryString(file);
        reader.onerror = e => {
            reject(new Error('文件读取失败！！！'))
        }
    })
}

const getChunkList = (raw: UploadRawFile) => {
    const chunkSize = 1024 * 1024 * 5, chunkList: ReqFile[] = []
    // 获取切片数组长度
    const chunkListLength = Math.ceil(raw.size / chunkSize)
    let chunkIndex = 0;
    for (let i = 0; i < chunkListLength; i++) {
        chunkList.push({ chunk: raw.slice(chunkIndex, chunkIndex + chunkSize), name: i })
        chunkIndex + chunkSize // 计算下一次切片开始位置
    }
    return chunkList;
}

const sendRequestChunkList = (chunkList: ReqFile[], fileMd5: string) => {
    const checkReqs = chunkList.map(item => {
        //start
        return axios({
            method: 'post',
            url: `http://localhost:10001/file-service/bigfile/check/chunk?fileMd5=${fileMd5}&chunkIndex=${item.name}`
        }).then(response => {
            const { data } = response;
            if (data.data.exist) {
                return Promise.resolve(response)
            }
            const formData = new FormData()
            formData.append('fileMd5', fileMd5)
            formData.append('chunk', item.chunk)
            formData.append('chunkIndex', String(item.name))
            return axios({
                method: 'post',
                url: `http://localhost:10001/file-service/bigfile/upload/chunk`,
                headers: { 'Content-Type': 'multipart/form-data' },
                data: formData
            })
        })
        // end
    })

    return checkReqs
}
/**

    // data.data.data.exist
    console.log(data);
})


 */
const fileList = ref<UploadUserFile[]>([
    {
        name: 'element-plus-logo.svg',
        url: 'https://element-plus.org/images/element-plus-logo.svg',
    },
    {
        name: 'element-plus-logo2.svg',
        url: 'https://element-plus.org/images/element-plus-logo.svg',
    },
])

const handleRemove: UploadProps['onRemove'] = (file, uploadFiles) => {
    console.log(file, uploadFiles)
}

const handlePreview: UploadProps['onPreview'] = (uploadFile) => {
    console.log(uploadFile)
}

const handleExceed: UploadProps['onExceed'] = (files, uploadFiles) => {
    ElMessage.warning(
        `The limit is 3, you selected ${files.length} files this time, add up to ${files.length + uploadFiles.length
        } totally`
    )
}

const beforeRemove: UploadProps['beforeRemove'] = (uploadFile, uploadFiles) => {
    return ElMessageBox.confirm(
        `Cancel the transfert of ${uploadFile.name} ?`
    ).then(
        () => true,
        () => false
    )
}
</script>

<style scoped>
.box {
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
}

.upload-demo {
    height: 50vh;
}

.el-progress--line {
    margin-bottom: 15px;
    width: 350px;
}
</style>