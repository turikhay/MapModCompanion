FROM node:18

ENV NODE_ENV=production

WORKDIR /usr/src/app

COPY package.json package-lock.json ./

RUN npm ci

COPY . .

ARG DEBUG=""
ENV DEBUG=${DEBUG}

CMD [ "node", "index.mjs" ]
