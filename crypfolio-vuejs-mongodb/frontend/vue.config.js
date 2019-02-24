// vue.config.js
module.exports = {
  // to development stage with vue ui (vue-cli-service serve)
  // proxy all webpack dev-server requests starting with /api
  // to our deployed java web application (backend localhost:8080) using http-proxy-middleware
  // see https://cli.vuejs.org/config/#devserver-proxy
  devServer: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        ws: true,
        changeOrigin: true
      }
    }
  },
  // Change build paths to make them Maven compatible
  // see https://cli.vuejs.org/config/
  outputDir: 'target/dist',
  assetsDir: 'static'
};