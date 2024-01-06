// const { defineConfig } = require('@vue/cli-service')
// module.exports = defineConfig({
//   transpileDependencies: true,
//   devServer: {
//
//     host: "localhost",
//     proxy:{
//       '/api':{
//         target : 'http://localhost:9090',
//         changeOrigin:true,
//         pathRewrite :{
//           '/api':''
//         }
//       }
//     }
//   },
// })
//
//
module.exports = {

  devServer: {

    host: "localhost",
    port:9876,
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    }
  }}
