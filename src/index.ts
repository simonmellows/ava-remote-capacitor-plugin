import { registerPlugin } from '@capacitor/core';

import type { AvaRemotePluginPlugin } from './definitions';

const AvaRemotePlugin = registerPlugin<AvaRemotePluginPlugin>('AvaRemotePlugin', {
  web: () => import('./web').then((m) => new m.AvaRemotePluginWeb()),
});

export * from './definitions';
export { AvaRemotePlugin };
