const path = require('path');

module.exports = {
  entry: './src/avisi/apps/tech_testing_ground/prototypes/htmx/atlas_kit/index.js',
  output: {
    filename: 'main.js',
    path: path.resolve(__dirname, './resources/public/js'),
  },
  resolve: {
    extensions: ['.js', '.jsx']
  },
  module: {
    rules: [{
      test: /\.(js|jsx)$/,
      exclude: /node_modules/,
      use: {
        loader: 'babel-loader'
      }
    },
      {
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
      }
    ]
  }
};
